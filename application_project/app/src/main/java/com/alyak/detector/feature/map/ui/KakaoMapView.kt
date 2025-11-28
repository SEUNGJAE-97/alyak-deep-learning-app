package com.alyak.detector.feature.map.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.alyak.detector.R
import com.alyak.detector.feature.map.data.model.LocationDto
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle

private const val TAG = "KakaoMapView"

@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val markerList by viewModel.places.collectAsState()
    val loc by viewModel.curLocation.collectAsState()
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(kakaoMapState, context)
    val routePath by viewModel.routePath.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 권한 요청 결과 처리
        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            // 모든 권한이 허용되었을 때
            Log.d(TAG, "모든 권한이 허용되었습니다.")
            viewModel.fetchLocation()
        } else {
            // 하나라도 거부된 권한이 있을 때
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(markerList) {
        Log.d("markerList", markerList.toString())
    }
    LaunchedEffect(Unit) {
        val permissionsToRequest = arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
        permissionLauncher.launch(permissionsToRequest)
    }

    LaunchedEffect(loc, kakaoMapState.value) {
        val kakaoMap = kakaoMapState.value ?: return@LaunchedEffect

        if (loc.latitude != 0.0 && loc.longitude != 0.0) {
            val position = LatLng.from(loc.latitude, loc.longitude)
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position, 15))

            val apiKey = "KakaoAK ${context.getString(R.string.REST_API_KEY)}"
            val categoryGroupCode = "HP8"
            val radius = 2000
            viewModel.fetchPlaces(
                apiKey,
                categoryGroupCode,
                loc.longitude.toString(),
                loc.latitude.toString(),
                radius
            )
        }
    }

    LaunchedEffect(markerList, kakaoMapState.value) {
        val kakaoMap = kakaoMapState.value ?: return@LaunchedEffect
        val labelManager = kakaoMap.labelManager ?: return@LaunchedEffect
        val labelLayer = labelManager.layer ?: return@LaunchedEffect
        val style = LabelStyles.from(
            "myStyleId",
            getLabelStyleByCategory(context, "PM9"),
            getLabelStyleByCategory(context, "HP8")
        )
        labelLayer.removeAll()
        markerList.forEach { place ->
            val lat = place.y.toDoubleOrNull() ?: return@forEach
            val lng = place.x.toDoubleOrNull() ?: return@forEach
            val position = LatLng.from(lat, lng)
            val label = LabelOptions.from(position)
                .setStyles(style)
            labelLayer.addLabel(label)
        }
    }

    LaunchedEffect(routePath, kakaoMapState.value) {
        val kakaoMap = kakaoMapState.value ?: return@LaunchedEffect
        Log.d("routePath", routePath.toString())
        if (routePath.isNotEmpty()) {
            drawPathOnMap(kakaoMap, routePath)

//            moveCameraToPath(kakaoMap, routePath)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.moveToCurrentLocationEvent.collect { location ->
            val kakaoMap = kakaoMapState.value
            if (kakaoMap != null) {
                val position = LatLng.from(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(position, 15)
                val cameraAnimation = CameraAnimation.from(500)
                kakaoMap.moveCamera(cameraUpdate, cameraAnimation)
                // 위치 갱신 시 주변 장소 갱신
                val apiKey = "KakaoAK ${context.getString(R.string.REST_API_KEY)}"
                viewModel.fetchPlaces(apiKey, "HP8", location.longitude.toString(), location.latitude.toString(), 2000)
            }
        }
    }

    AndroidView({ mapView }) { view -> }
}

@Composable
fun rememberMapViewWithLifecycle(
    kakaoMapState: MutableState<KakaoMap?>,
    context: Context
): View {
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
                            Toast.makeText(context, "지도를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }

                        // 지도 생명 주기 콜백: 지도 로딩 중 에러가 발생했을 때 호출
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
                            kakaoMapState.value = kakaoMap
                        }
                    }
                )
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                mapView.resume()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                mapView.pause()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                //FusedLocation stop
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

private fun drawPathOnMap(kakaoMap: KakaoMap, routePath: List<LocationDto>) {
    val layer = kakaoMap.routeLineManager?.layer
    layer?.removeAll()
    val points = routePath.map { dto ->
        LatLng.from(dto.latitude, dto.longitude)
    }

    val style = RouteLineStyle.from(
        20f,
        "#4F46E5".toColorInt()
    )

    val segment = RouteLineSegment.from(points, style)
    val options = RouteLineOptions.from(segment)
    layer?.addRouteLine(options)
}

//private fun moveCameraToPath(kakaoMap: KakaoMap, routePath: List<LocationDto>) {
//    if (routePath.isEmpty()) return
//
//    val points = routePath.map { LatLng.from(it.latitude, it.longitude) }
//
//    // 경로 전체를 포함하는 영역(Bounds) 생성
//    val bounds = CameraUpdateFactory.
//
//    // 100 padding을 주고 해당 영역으로 이동
//    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
//    kakaoMap.moveCamera(cameraUpdate)
//}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    KakaoMapView(
        modifier = Modifier.fillMaxSize()
    )
}