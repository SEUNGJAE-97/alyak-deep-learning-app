package com.alyak.detector.ui.map

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.alyak.detector.R
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelTextBuilder

@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    val markerList by viewModel.places.collectAsState()
    val context = LocalContext.current
    val apiKey = "KakaoAK ${context.getString(R.string.REST_API_KEY)}"
    val categoryGroupCode = "PM9"
    val x = "127.06283102249932"
    val y = "37.514322572335935"
    val radius = 500

    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val mapView = rememberMapViewWithLifecycle(
        onMapReady = { kakaoMap ->
            if (kakaoMapState.value == null) {
                kakaoMapState.value = kakaoMap
            }
        }
    )

    LaunchedEffect(Unit){
        Log.d("MapScreen", "fetchPlaces 호출됨")
        viewModel.fetchPlaces(apiKey, categoryGroupCode, x, y, radius)
    }
    // 지도 준비가 완료된 후 markerList가 바뀔 때만 라벨 갱신
    LaunchedEffect(markerList) {
        Log.d("MapScreen", "markerList 갱신됨: ${markerList.size}개")
        val kakaoMap = kakaoMapState.value
        if (kakaoMap != null) {
            val labelManager = kakaoMap.labelManager
            val labelLayer = labelManager?.layer
            labelLayer?.removeAll()
            markerList.forEach { place ->
                val latLng = LatLng.from(place.y.toDouble(), place.x.toDouble())
                val options = LabelOptions.from(latLng)
                    .setTexts(LabelTextBuilder().setTexts(place.place_name))
                labelLayer?.addLabel(options)

                Log.d("MapScreen", "placeName: ${place.place_name}, x: ${place.x}, y: ${place.y}")
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView }
    )
}


@Composable
fun rememberMapViewWithLifecycle(
    onMapReady : (KakaoMap) -> Unit
): MapView {
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
                onMapReady(kakaoMap)
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
