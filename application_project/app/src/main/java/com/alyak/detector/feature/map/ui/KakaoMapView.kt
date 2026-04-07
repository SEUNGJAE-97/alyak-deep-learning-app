package com.alyak.detector.feature.map.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.annotation.RawRes
import androidx.core.graphics.toColorInt
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alyak.detector.BuildConfig
import com.alyak.detector.R
import com.alyak.detector.feature.map.data.model.LocationDto
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.GestureType
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
import kotlinx.coroutines.isActive

private const val TAG = "KakaoMapView"

@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val markerList by viewModel.displayedPlaces.collectAsState()
    val context = LocalContext.current
    val hospitalPinStyle = remember(context) {
        lottieRawResToLabelStyle(context, R.raw.hospital_pin, R.drawable.hospital)
    }
    val pharmacyPinStyle = remember(context) {
        lottieRawResToLabelStyle(context, R.raw.pharmacy_pin, R.drawable.pharmacy)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    var locationGranted by remember { mutableStateOf(false) }
    val mapView = rememberMapViewWithLifecycle(kakaoMapState, context) {
        viewModel.syncMapCameraToStoredLocation()
    }
    val routePath by viewModel.routePath.collectAsState()
    val curLocation by viewModel.curLocation.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 권한 요청 결과 처리
        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            Log.d(TAG, "모든 권한이 허용되었습니다.")
            locationGranted = true
            viewModel.startContinuousLocationTracking()
        } else {
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

    DisposableEffect(lifecycleOwner, locationGranted) {
        if (!locationGranted) {
            return@DisposableEffect onDispose { }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startContinuousLocationTracking()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopContinuousLocationTracking()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopContinuousLocationTracking()
        }
    }

    DisposableEffect(kakaoMapState.value) {
        val kakaoMap = kakaoMapState.value ?: return@DisposableEffect onDispose { }
        val listener = KakaoMap.OnCameraMoveEndListener { _, cameraPosition, _: GestureType ->
            val latLng = cameraPosition.position
            viewModel.updateCameraMapCenter(latLng.latitude, latLng.longitude)
        }
        kakaoMap.setOnCameraMoveEndListener(listener)
        onDispose {
            kakaoMap.setOnCameraMoveEndListener(null)
        }
    }

    LaunchedEffect(markerList, kakaoMapState.value) {
        val kakaoMap = kakaoMapState.value ?: return@LaunchedEffect
        val labelManager = kakaoMap.labelManager ?: return@LaunchedEffect
        val labelLayer = labelManager.layer ?: return@LaunchedEffect
        labelLayer.removeAll()
        markerList.forEach { place ->
            val lat = place.y.toDoubleOrNull() ?: return@forEach
            val lng = place.x.toDoubleOrNull() ?: return@forEach
            val position = LatLng.from(lat, lng)
            val pinStyle = when (place.category_group_code) {
                "PM9" -> pharmacyPinStyle
                "HP8" -> hospitalPinStyle
                else -> hospitalPinStyle
            }
            val styles = LabelStyles.from(
                "place_${place.id}",
                pinStyle,
            )
            val label = LabelOptions.from(position).setStyles(styles)
            labelLayer.addLabel(label)
        }
    }

    LaunchedEffect(routePath, kakaoMapState.value) {
        val kakaoMap = kakaoMapState.value ?: return@LaunchedEffect
        Log.d("routePath", routePath.toString())
        if (routePath.isNotEmpty()) {
            drawPathOnMap(kakaoMap, routePath)
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
                val apiKey = "KakaoAK ${BuildConfig.REST_API_KEY}"
                viewModel.fetchPlaces(
                    apiKey,
                    location.longitude.toString(),
                    location.latitude.toString(),
                    2000,
                )
            }
        }
    }

    Box(modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { },
        )
        if (locationGranted) {
            MyLocationPulseOverlay(
                kakaoMap = kakaoMapState.value,
                location = curLocation,
            )
        }
    }
}

private val MyLocationDotSize = 64.dp

@Composable
private fun MyLocationPulseOverlay(
    kakaoMap: KakaoMap?,
    location: LocationDto,
) {
    val density = LocalDensity.current
    var screenPoint by remember { mutableStateOf<Point?>(null) }
    val latestMap by rememberUpdatedState(kakaoMap)
    val latestLocation by rememberUpdatedState(location)

    LaunchedEffect(kakaoMap) {
        if (kakaoMap == null) return@LaunchedEffect
        while (isActive) {
            withFrameNanos { }
            val map = latestMap ?: continue
            val loc = latestLocation
            screenPoint = map.toScreenPoint(LatLng.from(loc.latitude, loc.longitude))
        }
    }

    val point = screenPoint ?: return
    val half = MyLocationDotSize / 2
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.red_pulsing_dot))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .offset(
                x = with(density) { point.x.toDp() } - half,
                y = with(density) { point.y.toDp() } - half,
            )
            .size(MyLocationDotSize),
    )
}

@Composable
fun rememberMapViewWithLifecycle(
    kakaoMapState: MutableState<KakaoMap?>,
    context: Context,
    onMapReady: () -> Unit = {},
): View {
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val latestOnMapReady by rememberUpdatedState(onMapReady)

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
                            latestOnMapReady()
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

/**
 * `res/raw`의 Lottie 핀을 첫 화면에 가깝게 렌더링해 카카오 [LabelStyle]로 만듭니다.
 * 로드 실패 시 [fallbackDrawable]을 사용합니다.
 */
private fun lottieRawResToLabelStyle(
    context: Context,
    @RawRes rawRes: Int,
    fallbackDrawable: Int,
): LabelStyle {
    val result = LottieCompositionFactory.fromRawResSync(context, rawRes)
    val composition = result.value
    if (composition == null) {
        Log.e(TAG, "Lottie pin load failed (raw=$rawRes)", result.exception)
        return LabelStyle.from(fallbackDrawable).setZoomLevel(10).setApplyDpScale(true)
    }
    val drawable = LottieDrawable()
    drawable.composition = composition
    val bounds = composition.bounds
    val srcW = bounds.width().toInt().coerceAtLeast(1)
    val srcH = bounds.height().toInt().coerceAtLeast(1)
    drawable.setBounds(0, 0, srcW, srcH)
    // 일부 애니메는 0프레임에서 투명 — 중간 프레임을 스냅샷으로 사용
    drawable.progress = 0.5f
    val srcBitmap = Bitmap.createBitmap(srcW, srcH, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(srcBitmap)
    drawable.draw(canvas)
    val density = context.resources.displayMetrics.density
    val targetPx = (48f * density).toInt().coerceAtLeast(32)
    val scale = targetPx / srcW.toFloat()
    val outW = (srcW * scale).toInt().coerceAtLeast(1)
    val outH = (srcH * scale).toInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(srcBitmap, outW, outH, true)
    if (!srcBitmap.isRecycled && srcBitmap != scaled) {
        srcBitmap.recycle()
    }
    return LabelStyle.from(scaled).setZoomLevel(10).setApplyDpScale(true)
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

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    KakaoMapView(
        modifier = Modifier.fillMaxSize()
    )
}