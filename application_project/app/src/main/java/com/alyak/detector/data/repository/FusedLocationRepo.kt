package com.alyak.detector.data.repository

import android.content.Context
import com.alyak.detector.data.dto.LocationDto

class FusedLocationRepo(private val context: Context) : LocationRepo {
    override suspend fun getCurrentLocation(): LocationDto {
        TODO("Not yet implemented")
    }

    override fun startLocationUpdate(callback: (LocationDto) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun stopLocationUpdate() {
        TODO("Not yet implemented")
    }

}