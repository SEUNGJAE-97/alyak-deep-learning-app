package com.alyak.detector.ui.map

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.content.ContextCompat
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
import com.alyak.detector.util.PermissionManager
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
    val kakaoMapState = remember { mutableStateOf<KakaoMap?>(null) }
    val markerList by viewModel.places.collectAsState()
    val loc by viewModel.curLocation.collectAsState()
    val context = LocalContext.current

    val apiKey = "KakaoAK ${context.getString(R.string.REST_API_KEY)}"
    val categoryGroupCode = "HP8"
    val x = loc.latitude
    val y = loc.longitude
    val radius = 2000
    val mapView = rememberMapViewWithLifecycle(kakaoMapState, context)
    val permissionManager = PermissionManager(context)

    LaunchedEffect(Unit) {
        permissionManager.requestPermissions()
        runCatching {
            permissionManager.checkPermission(
                context,
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
            )
        }.onSuccess {
            viewModel.fetchLocation()
            Log.d(TAG, "MapScreen: $loc")
        }.onFailure {
            permissionManager.moveToSettings()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchPlaces(apiKey, categoryGroupCode, x.toString(), y.toString(), radius)
    }
    LaunchedEffect(markerList) {
        Log.d(TAG, "MapScreen: $markerList")
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



    AndroidView({ mapView }) { view -> }
}

@Composable
fun rememberMapViewWithLifecycle(
    kakaoMapState: MutableState<KakaoMap?>,
    context: Context
): View {
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = context as? AppCompatActivity ?: throw IllegalStateException("Activity가 아닙니다.")
    val permissionManager = remember { PermissionManager(activity) }

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
                            Toast.makeText(context, "지도를 불러오는중 에러가 발생했습니다..", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun getPosition(): LatLng {
                            return super.getPosition()
                        }

                        override fun onMapReady(kakaoMap: KakaoMap) {
                            // 1. 퍼미션 받기
                            permissionManager.requestPermissions()

                            kakaoMapState.value = kakaoMap

                            val position = LatLng.from(37.2, 127.1)
                            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position))

                            // 2. 라벨 매니저 초기화 및 라벨 추가
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

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                //FusedLocation start
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

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    MapScreen(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize()
    )
}