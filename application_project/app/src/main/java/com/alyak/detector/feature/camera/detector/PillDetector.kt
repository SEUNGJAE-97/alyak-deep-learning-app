package com.alyak.detector.feature.camera.detector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp

// 결과를 담을 데이터 클래스 (PillAnalyzer에서 사용)
data class PillDetection(
    val boundingBox: RectF,
    val score: Float
)

class PillDetector(context: Context) {
    // 1. 모델 로드 (v2_float32.tflite 파일명 확인!)
    private val modelBuffer = FileUtil.loadMappedFile(context, "v3_int8.tflite")
    private val interpreter = Interpreter(modelBuffer)
    
    // 2. 전처리 설정 (정사각형 자르기 + 리사이즈 + 정규화)
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeWithCropOrPadOp(640,640)) // 화면 중앙 기준으로 정사각형으로 자름 (오버레이 영역)
        .add(ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR)) // YOLOv11 입력 사이즈
        .add(NormalizeOp(0.0f, 255.0f)) // 0~1 사이로 정규화 (메타데이터 에러 해결 핵심)
        .build()

    @OptIn(ExperimentalGetImage::class)
    fun processImage(
        imageProxy: ImageProxy,
        onSuccess: (List<PillDetection>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            if (bitmap == null) {
                imageProxy.close()
                return
            }

            // 이미지 전처리 실행
            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)

            // YOLOv11 출력 텐서 준비: [1, 5, 8400]
            val output = Array(1) { Array(5) { FloatArray(8400) } }

            // 모델 추론
            interpreter.run(tensorImage.buffer, output)

            // 후처리: 결과 해석 (단순화된 버전)
            val results = mutableListOf<PillDetection>()
            val CONFIDENCE_THRESHOLD = 0.5f

            // [cx, cy, w, h, score]
            for (i in 0 until 8400) {
                // 0. 점수
                val score = output[0][4][i]
                // 1. 박스 좌표 추출 (cx, cy, w, h)
                val cx = output[0][0][i]
                val cy = output[0][1][i]
                val w = output[0][2][i]
                val h = output[0][3][i]

                // 2. 임계값 이상이면 박스 생성
                if (score > CONFIDENCE_THRESHOLD) {
                    val left = (cx - w / 2f).coerceIn(0f, 1f)
                    val top = (cy - h / 2f).coerceIn(0f, 1f)
                    val right = (cx + w / 2f).coerceIn(0f, 1f)
                    val bottom = (cy + h / 2f).coerceIn(0f, 1f)

                    results.add(PillDetection(RectF(left, top, right, bottom), score))
                }
            }
            onSuccess(nms(results, iouThreshold = 0.45f))

        } catch (e: Exception) {
            onFailure(e)
        } finally {
            imageProxy.close()
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        return imageProxy.toBitmap()
    }

    private fun nms(detections: List<PillDetection>, iouThreshold: Float): List<PillDetection> {
        if (detections.isEmpty()) return emptyList()

        // 1. 점수(Score) 내림차순으로 정렬
        val sortedDetections = detections.sortedByDescending { it.score }.toMutableList()
        val selectedDetections = mutableListOf<PillDetection>()

        while (sortedDetections.isNotEmpty()) {
            // 2. 가장 점수가 높은 박스를 선택하고 결과 리스트에 추가
            val first = sortedDetections.removeAt(0)
            selectedDetections.add(first)

            // 3. 남은 박스들과 방금 선택한 박스의 겹침 정도(IoU)를 비교하여 많이 겹치면 제거
            val iterator = sortedDetections.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (calculateIoU(first.boundingBox, next.boundingBox) > iouThreshold) {
                    iterator.remove()
                }
            }
        }
        return selectedDetections
    }

    private fun calculateIoU(rect1: RectF, rect2: RectF): Float {
        val intersection = RectF()
        if (!intersection.setIntersect(rect1, rect2)) return 0f

        val intersectionArea = intersection.width() * intersection.height()
        val rect1Area = rect1.width() * rect1.height()
        val rect2Area = rect2.width() * rect2.height()
        val unionArea = rect1Area + rect2Area - intersectionArea

        return intersectionArea / unionArea
    }
}