package com.alyak.detector.feature.camera.detector

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

/**
 * 카메라 프레임을 받아 PillDetector에게 분석을 요청하는 클래스
 * @param detector: 실제 TFLite 모델 추론을 수행하는 객체
 * @param onResult: 분석 결과를 UI 레이어(Compose)로 전달하는 콜백 함수
 */
class PillAnalyzer(
    private val detector: PillDetector,
    private val onResult: (List<PillDetection>, Int, Int) -> Unit
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // 1. 이미지 회전 정보에 따라 가로/세로 크기 결정
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        // 카메라 프레임의 실제 너비와 높이 (회전 고려)
        val isRotated = rotationDegrees == 90 || rotationDegrees == 270
        val width = if (isRotated) imageProxy.height else imageProxy.width
        val height = if (isRotated) imageProxy.width else imageProxy.height

        // 2. PillDetector를 이용해 이미지 분석 실행
        detector.processImage(
            imageProxy = imageProxy,
            onSuccess = { detections ->
                // 성공 시 UI 콜백으로 결과 전달
                onResult(detections, width, height)
            },
            onFailure = { error ->
                // 실패 시 로그 출력
                Log.e("PillAnalyzer", "모델 분석 실패: ${error.message}")
            }
        )
    }
}