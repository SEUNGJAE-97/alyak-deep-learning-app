package com.alyak.detector.feature.camera.data.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
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

// 결과를 담을 데이터 클래스 (PillAnalyzer에서 사용)
data class PillDetection(
    val boundingBox: RectF,
    val score: Float
)

class PillDetector(context: Context) {
    // 1. 모델 로드
    private val modelBuffer = FileUtil.loadMappedFile(context, "v3_int8.tflite")
    private val interpreter = Interpreter(modelBuffer)

    // 2. 전처리 설정
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR)) // YOLOv11 입력 사이즈
        .add(NormalizeOp(0.0f, 255.0f))// 정규화
        .build()

    fun processImage(
        bitmap: Bitmap,
        onSuccess: (List<PillDetection>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)
            val output = Array(1) { Array(5) { FloatArray(8400) } }
            interpreter.run(tensorImage.buffer, output)

            val results = postProcess(output)
            onSuccess(results)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

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

            processImage(
                bitmap = bitmap,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        } catch (e: Exception) {
            onFailure(e)
        } finally {
            imageProxy.close()
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        return imageProxy.toBitmap()
    }

    private fun postProcess(output: Array<Array<FloatArray>>): List<PillDetection> {
        val results = mutableListOf<PillDetection>()
        val CONFIDENCE_THRESHOLD = 0.5f

        for (i in 0 until 8400) {
            val score = output[0][4][i]
            val cx = output[0][0][i]
            val cy = output[0][1][i]
            val w = output[0][2][i]
            val h = output[0][3][i]

            if (score > CONFIDENCE_THRESHOLD) {
                val left = (cx - w / 2f).coerceIn(0f, 1f)
                val top = (cy - h / 2f).coerceIn(0f, 1f)
                val right = (cx + w / 2f).coerceIn(0f, 1f)
                val bottom = (cy + h / 2f).coerceIn(0f, 1f)

                results.add(PillDetection(RectF(left, top, right, bottom), score))
            }
        }

        return nms(results, iouThreshold = 0.45f)
    }

    private fun nms(detections: List<PillDetection>, iouThreshold: Float): List<PillDetection> {
        if (detections.isEmpty()) return emptyList()

        val sortedDetections = detections.sortedByDescending { it.score }.toMutableList()
        val selectedDetections = mutableListOf<PillDetection>()

        while (sortedDetections.isNotEmpty()) {
            val first = sortedDetections.removeAt(0)
            selectedDetections.add(first)

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