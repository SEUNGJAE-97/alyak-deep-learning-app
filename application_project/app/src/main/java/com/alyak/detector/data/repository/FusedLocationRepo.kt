package com.alyak.detector.data.repository

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.os.Looper
import android.util.Log
import com.alyak.detector.data.dto.LocationDto
import com.alyak.detector.util.PermissionManager
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FusedLocationRepo @Inject constructor(
    @ApplicationContext private val context: Context

) : LocationRepo {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback

    @Suppress("MissingPermission")
    override suspend fun getCurrentLocation(): LocationDto = suspendCancellableCoroutine { cont ->
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(5000)
            .setDurationMillis(10000)
            .build()
        val cancellationTokenSource = CancellationTokenSource()
        val cancellationToken = cancellationTokenSource.token

        fusedLocationClient.getCurrentLocation(request, cancellationToken)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val dto = LocationDto(location.latitude, location.longitude)
                    cont.resume(dto)
                }
            }
    }

    @Suppress("MissingPermission")
    override suspend fun startLocationUpdate(callback: (LocationDto) -> Unit) {
        runCatching {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L
            ).apply {
                setMinUpdateIntervalMillis(5000L)
                setWaitForAccurateLocation(true)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        }.onFailure { e ->
            Log.e("startLocationUpdate", "위치 객체를 생성하지 못했어요")
        }
    }

    override suspend fun stopLocationUpdate() {
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}