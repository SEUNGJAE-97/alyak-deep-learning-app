package com.alyak.detector.ui.map

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.sdk.common.util.Utility

@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val mapView = rememberMapViewWithLifecycle()
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView }
    )

}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    mapView.start(
        object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("MapView", "Map is destroyed.")
            }

            override fun onMapError(p0: Exception?) {
                Log.d("MapView", "Map is occured Error.")
            }
        },
        object : KakaoMapReadyCallback() {
            override fun getPosition(): LatLng {
                return LatLng.from(37.5665, 126.9780)
            }

            override fun onMapReady(kakaoMap: KakaoMap) {
                // 지도 준비가 완료되면 호출됩니다.
                // 여기서 지도 설정 및 마커 추가 등의 작업을 수행합니다.
                //var ps = kakaoMap.keyword
            }
        }
    )

    return mapView
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    MapScreen(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize()
    )
}
