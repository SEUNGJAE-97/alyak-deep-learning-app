package com.alyak.detector.ui.map

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.alyak.detector.R
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

private const val TAG = "MapScreen"

@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    val markerList by viewModel.places.collectAsState()
    val context = LocalContext.current
    val apiKey = "KakaoAK ${context.getString(R.string.REST_API_KEY)}"
    val categoryGroupCode = "HP8"
    val x = "127.1"
    val y = "37.2"
    val radius = 2000
    val mapView = rememberMapViewWithLifecycle { map ->
        kakaoMap = map
        map.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(37.2, 127.1)))
    }

    LaunchedEffect(Unit) {
        viewModel.fetchPlaces(apiKey, categoryGroupCode, x, y, radius)
    }
    LaunchedEffect(markerList) {
        Log.d(TAG, "MapScreen: $markerList")
        kakaoMap?.labelManager?.let { manager ->
            manager.layer?.removeAll() // 기존 라벨 제거
            markerList.forEach { place ->
                val pos = LatLng.from(place.y.toDouble(), place.x.toDouble())
                val options = LabelOptions.from(pos)
                manager.layer?.addLabel(options)
            }
        }

    }


    AndroidView({ mapView }) { view -> }
}

@Composable
fun rememberMapViewWithLifecycle(
    onMapReady: (KakaoMap) -> Unit
): View {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                mapView.start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {
                            Toast.makeText(context, "지도를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }

                        override fun onMapError(exception: Exception?) {
                            Toast.makeText(context, "지도를 불러오는중 에러가 발생했습니다..", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun getPosition(): LatLng {
                            return super.getPosition()
                        }

                        override fun onMapReady(kakaoMap: KakaoMap) {
                            val position = LatLng.from(37.2, 127.1)
                            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position))

                            val labelManager: LabelManager? = kakaoMap.labelManager
                            labelManager?.let { manager ->
                                val style = LabelStyles.from(
                                    "myStyleId",
                                    getLabelStyleByCategory(context, "PM9"),
                                    getLabelStyleByCategory(context, "HP8")
                                )
                                manager.addLabelStyles(style)
                                val labelOptions = LabelOptions.from(position).setStyles(style)
                                manager.layer?.addLabel(labelOptions)
                            }
                        }
                    }
                )
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                mapView.resume()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                mapView.pause()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    return mapView
}

private fun getLabelStyleByCategory(context: Context, category: String): LabelStyle {
    val mark =
        when (category) {
            "PM9" -> R.drawable.pharmacy
            "HP8" -> R.drawable.pharmacy
            else -> R.drawable.hospital
        }
    return LabelStyle.from(mark).setZoomLevel(10).setApplyDpScale(true)
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    MapScreen(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize()
    )
}
