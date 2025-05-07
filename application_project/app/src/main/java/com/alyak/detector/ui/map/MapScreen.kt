package com.alyak.detector.ui.map

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
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
import com.alyak.detector.data.api.KakaoLocalApi
import com.kakao.sdk.friend.m.s
import com.kakao.vectormap.RoadViewRequest.Marker
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MapScreen"
@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    val markerList by viewModel.places.collectAsState()
    val context = LocalContext.current
    val apiKey = "KakaoAK ${context.getString(R.string.REST_API_KEY)}"
    val categoryGroupCode = "HP8"
    val x = "127.1"
    val y = "37.2"
    val radius = 2000
    val mapView = rememberMapViewWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchPlaces(apiKey, categoryGroupCode, x, y, radius)
    }
    LaunchedEffect(markerList) {
        Log.d(TAG, "MapScreen: $markerList")
    }
    LaunchedEffect(markerList, mapView) {
//        val kakaoMap = mapView.kakaoMap ?: return@LaunchedEffect
//        val labelManager = kakaoMap.labelManager ?: return@LaunchedEffect
//        val labelLayer = labelManager.layer ?: return@LaunchedEffect
//
//        // 기존 라벨 제거
//        labelLayer.clear()
//
//        // 스타일 준비
//        val style = LabelStyles.from(
//            "dynamicStyle",
//            getLabelStyleByCategory(context, "PM9"),
//            getLabelStyleByCategory(context, "HP8")
//        )
//        labelManager.addLabelStyles(style)
//
//        // 새 라벨 추가
//        markerList.forEach { place ->
//            val lat = place.y.toDoubleOrNull() ?: return@forEach
//            val lng = place.x.toDoubleOrNull() ?: return@forEach
//            val position = LatLng.from(lat, lng)
//
//            val label = LabelOptions.from(position)
//                .setStyles(style)
//                .setTexts(
//                    LabelTextBuilder.from(place.place_name)
//                        .setTextStyles(LabelTextStyle.from(Color.BLACK, 14f))
//                )
//            labelLayer.addLabel(label)
//        }
//
//        // 중심 카메라 옮기고 싶다면 여기서
//        // kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(...))
    }


    AndroidView({ mapView }) { view -> }
}

@Composable
fun rememberMapViewWithLifecycle(): View {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                mapView.start(
                    object : MapLifeCycleCallback() {
                        // 지도 생명 주기 콜백: 지도가 파괴될 때 호출
                        override fun onMapDestroy() {
                            // 필자가 직접 만든 Toast생성 함수
                            Toast.makeText(context, "지도를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }

                        // 지도 생명 주기 콜백: 지도 로딩 중 에러가 발생했을 때 호출
                        override fun onMapError(exception: Exception?) {
                            // 필자가 직접 만든 Toast생성 함수
                            Toast.makeText(context, "지도를 불러오는중 에러가 발생했습니다..", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun getPosition(): LatLng {
                            return super.getPosition()
                        }

                        override fun onMapReady(kakaoMap: KakaoMap) {
                            // 기본 카메라 위치 설정
                            val position = LatLng.from(37.2, 127.1)
                            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position))

                            // 라벨 매니저 초기화 및 라벨 추가
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

fun getLabelStyleByCategory(context: Context, category: String): LabelStyle {
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
