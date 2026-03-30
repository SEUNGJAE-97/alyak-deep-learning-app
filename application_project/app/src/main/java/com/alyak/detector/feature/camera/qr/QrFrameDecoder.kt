package com.alyak.detector.feature.camera.qr

import android.graphics.Rect
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer

/**
 * CameraX ImageAnalysis 프레임을 ZXing으로 QR 디코딩하기 위한 전처리/디코더 유틸입니다.
 *
 * - ImageProxy의 첫 plane(Y)를 luminance로 사용
 * - 회전 보정(0/90/180/270)
 * - 화면 오버레이 프레임에 맞춰 가운데 ROI만 디코딩 시도
 */
fun decodeQrFromImageProxy(
    reader: MultiFormatReader,
    image: ImageProxy,
    overlayFrameRatio: Float,
    qrDecodeInsetRatio: Float,
): String? {
    val plane = image.planes.firstOrNull() ?: return null
    val buffer = plane.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val width = image.width
    val height = image.height
    if (width <= 0 || height <= 0) return null

    val rotation = image.imageInfo.rotationDegrees % 360
    val (data, w, h) = when (rotation) {
        90 -> Triple(rotateYPlane90(bytes, width, height), height, width)
        180 -> Triple(rotateYPlane180(bytes, width, height), width, height)
        270 -> Triple(rotateYPlane270(bytes, width, height), height, width)
        else -> Triple(bytes, width, height)
    }

    // 오버레이 프레임 내부(약간 안쪽 포함) 영역만 QR 디코딩에 사용
    val rect = cropCenterRect(w, h, overlayFrameRatio * qrDecodeInsetRatio)
    val source = PlanarYUVLuminanceSource(
        data,
        w,
        h,
        rect.left,
        rect.top,
        rect.width(),
        rect.height(),
        false
    )

    val bitmap = BinaryBitmap(HybridBinarizer(source))
    return try {
        val result: Result = reader.decodeWithState(bitmap)
        result.text
    } catch (_: Exception) {
        null
    } finally {
        reader.reset()
    }
}

private fun cropCenterRect(w: Int, h: Int, ratio: Float): Rect {
    val size = (minOf(w, h) * ratio).toInt().coerceAtLeast(1)
    val left = ((w - size) / 2).coerceAtLeast(0)
    val top = ((h - size) / 2).coerceAtLeast(0)
    return Rect(left, top, (left + size).coerceAtMost(w), (top + size).coerceAtMost(h))
}

private fun rotateYPlane90(src: ByteArray, width: Int, height: Int): ByteArray {
    val dst = ByteArray(src.size)
    var i = 0
    for (x in 0 until width) {
        for (y in height - 1 downTo 0) {
            dst[i++] = src[y * width + x]
        }
    }
    return dst
}

private fun rotateYPlane180(src: ByteArray, width: Int, height: Int): ByteArray {
    val dst = ByteArray(src.size)
    var i = 0
    for (p in src.size - 1 downTo 0) {
        dst[i++] = src[p]
    }
    return dst
}

private fun rotateYPlane270(src: ByteArray, width: Int, height: Int): ByteArray {
    val dst = ByteArray(src.size)
    var i = 0
    for (x in width - 1 downTo 0) {
        for (y in 0 until height) {
            dst[i++] = src[y * width + x]
        }
    }
    return dst
}

