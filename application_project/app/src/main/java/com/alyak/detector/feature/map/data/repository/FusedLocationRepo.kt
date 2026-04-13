package com.alyak.detector.feature.map.data.repository

import android.content.Context
import android.os.Looper
import android.util.Log
import com.alyak.detector.feature.map.data.model.LocationDto
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FusedLocationRepo @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationRepo {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var locationConsumer: ((LocationDto) -> Unit)? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            val dto = LocationDto(loc.latitude, loc.longitude)
            persistLocation(dto)
            locationConsumer?.invoke(dto)
        }
    }

    @Suppress("MissingPermission")
    override suspend fun getCurrentLocation(): LocationDto? = suspendCancellableCoroutine { cont ->
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(10_000)
            .setDurationMillis(15_000)
            .build()
        val cancellationToken = CancellationTokenSource().token

        fusedLocationClient.getCurrentLocation(request, cancellationToken)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val dto = LocationDto(location.latitude, location.longitude)
                    persistLocation(dto)
                    cont.resume(dto)
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }

    override suspend fun getInitialLocationForMap(): LocationDto? {
        readPersistedLocation()?.let { return it }
        return getLastKnownLocationSuspend()
    }

    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocationSuspend(): LocationDto? =
        suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val dto = LocationDto(location.latitude, location.longitude)
                        persistLocation(dto)
                        cont.resume(dto)
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener { cont.resume(null) }
        }

    private fun readPersistedLocation(): LocationDto? {
        if (!prefs.contains(KEY_LAT) || !prefs.contains(KEY_LNG)) return null
        val lat = prefs.getFloat(KEY_LAT, Float.NaN).toDouble()
        val lng = prefs.getFloat(KEY_LNG, Float.NaN).toDouble()
        if (lat.isNaN() || lng.isNaN()) return null
        return LocationDto(lat, lng)
    }

    private fun persistLocation(dto: LocationDto) {
        prefs.edit()
            .putFloat(KEY_LAT, dto.latitude.toFloat())
            .putFloat(KEY_LNG, dto.longitude.toFloat())
            .apply()
    }

    /**
     * 도보 기준: 균형 잡힌 정확도, 약 3초 간격.
     * 맵 화면이 포그라운드일 때만 [startLocationUpdate] 호출 권장.
     */
    @Suppress("MissingPermission")
    override fun startLocationUpdate(onLocation: (LocationDto) -> Unit) {
        runCatching {
            stopLocationUpdateInternal()
            locationConsumer = onLocation
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                LOCATION_UPDATE_INTERVAL_MS,
            ).apply {
                setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL_MS)
                setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL_MS * 2)
                setWaitForAccurateLocation(false)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
        }.onFailure { e ->
            Log.e(TAG, "startLocationUpdate 실패", e)
        }
    }

    override fun stopLocationUpdate() {
        stopLocationUpdateInternal()
    }

    private fun stopLocationUpdateInternal() {
        runCatching {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        locationConsumer = null
    }

    companion object {
        private const val TAG = "FusedLocationRepo"
        private const val PREFS_NAME = "map_last_known_location"
        private const val KEY_LAT = "lat"
        private const val KEY_LNG = "lng"
        private const val LOCATION_UPDATE_INTERVAL_MS = 3_000L
    }
}
