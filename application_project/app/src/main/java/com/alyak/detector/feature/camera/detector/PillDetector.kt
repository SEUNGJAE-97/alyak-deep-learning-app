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
    val boundingBox: android.graphics.RectF,
    val score: Float
)

class PillDetector(context: Context) {
    // 1. 모델 로드 (v2_float32.tflite 파일명 확인!)
    private val modelBuffer = FileUtil.loadMappedFile(context, "v2_float32.tflite")
    private val interpreter = Interpreter(modelBuffer)
    init {
        // 출력 텐서 개수
        val outputCount = interpreter.outputTensorCount
        for (i in 0 until outputCount) {
            val tensor = interpreter.getOutputTensor(i)
            Log.d(
                "PillDetector",
                "output[$i] shape=${tensor.shape().contentToString()}, type=${tensor.dataType()}"
            )
        }
    }
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
            // ImageProxy를 Bitmap으로 변환 (직접 구현하거나 확장 프로그램 사용)
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
            // (클래스가 1개일 때: x, y, w, h, score 총 5개)
            val output = Array(1) { Array(104) { FloatArray(8400) } }

            // 모델 추론
            interpreter.run(tensorImage.buffer, output)

            // 후처리: 결과 해석 (단순화된 버전)
            val results = mutableListOf<PillDetection>()
            val CONFIDENCE_THRESHOLD = 0.5f

            for (i in 0 until 8400) {
                // 1. 박스 좌표 추출 (cx, cy, w, h) - 0~640 정규화
                val cx = output[0][0][i]
                val cy = output[0][1][i]
                val w = output[0][2][i]
                val h = output[0][3][i]

                // 2. 최대 클래스 스코어 찾기 (채널 4~103)
                var maxScore = 0f
                for (c in 4 until 104) {
                    val score = output[0][c][i]
                    if (score > maxScore) maxScore = score
                }

                // 3. threshold 이상이면 박스 생성
                if (maxScore > CONFIDENCE_THRESHOLD) {
                    val left = (cx - w / 2f).coerceIn(0f, 640f)
                    val top = (cy - h / 2f).coerceIn(0f, 640f)
                    val right = (cx + w / 2f).coerceIn(0f, 640f)
                    val bottom = (cy + h / 2f).coerceIn(0f, 640f)

                    val box = RectF(left, top, right, bottom)
                    results.add(PillDetection(box, maxScore))

                    Log.d("PillDetector", "DETECTED: box=($left,$top,$right,$bottom), score=$maxScore")
                }
            }

            Log.d("PillDetector", "Total detections: ${results.size}")
            onSuccess(results)

        } catch (e: Exception) {
            onFailure(e)
        } finally {
            imageProxy.close() // 반드시 닫아야 다음 프레임이 들어옴
        }
    }

    // ImageProxy -> Bitmap 변환 헬퍼 (YUV_420_888 대응 필요)
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        return imageProxy.toBitmap()
    }
}