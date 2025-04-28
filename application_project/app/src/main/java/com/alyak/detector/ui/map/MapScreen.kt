package com.alyak.detector.ui.map

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle

@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    var isMapReady by remember { mutableStateOf(false) }
    val markerList by viewModel.places.collectAsState()
    val context = LocalContext.current
    val apiKey = "KakaoAK ${context.getString(R.string.REST_API_KEY)}"
    val categoryGroupCode = "PM9"
    val x = "127"
    val y = "37"
    val radius = 2000

    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val mapView = rememberMapViewWithLifecycle(
        onMapReady = { kakaoMap ->
            kakaoMapState.value = kakaoMap
            isMapReady = true
        }
    )

    LaunchedEffect(Unit) {
        Log.d("MapScreen", "fetchPlaces 호출됨")
        viewModel.fetchPlaces(apiKey, categoryGroupCode, x, y, radius)
    }

    LaunchedEffect(markerList, kakaoMapState.value, isMapReady) {
        if (!isMapReady) {
            Log.d("MapScreen", "Map is not ready yet")
            return@LaunchedEffect
        }

        val kakaoMap = kakaoMapState.value
        val labelManager = try {
            kakaoMap?.labelManager
        } catch (e: Exception) {
            null
        }
        val labelLayer = labelManager?.layer

        if (kakaoMap == null || labelManager == null || labelLayer == null) {
            Log.d("MapScreen", "kakaoMap, labelManager, or labelLayer is not ready")
            return@LaunchedEffect
        }

        labelLayer.removeAll()

        markerList.forEach { place ->
            try {
                val lat = place.y.toDouble()
                val lng = place.x.toDouble()
                val labelText = place.place_name ?: "Unknown"
                val latLng = LatLng.from(lat, lng)

                val options = LabelOptions.from(latLng)
                    .setTexts(LabelTextBuilder().setTexts(labelText))
                labelLayer.addLabel(options)

                Log.d("MapScreen", "Added label: $labelText ($lat, $lng)")
            } catch (e: Exception) {
                Log.e("MapScreen", "Error adding label: ${e.localizedMessage}")
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
    onMapReady: (KakaoMap) -> Unit
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
