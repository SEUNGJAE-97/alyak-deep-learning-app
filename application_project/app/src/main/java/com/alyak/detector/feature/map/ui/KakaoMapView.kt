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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.alyak.detector.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

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

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    KakaoMapView(
        modifier = Modifier.fillMaxSize()
    )
}