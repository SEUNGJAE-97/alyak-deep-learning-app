package com.alyak.detector.feature.user.repository

import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.user.data.api.UserService

class UserRepository(private val userService : UserService) {

    suspend fun logoutUser(): ApiResult<Unit> =
        safeCall { userService.logoutUser() }

    suspend fun deleteUser(): ApiResult<Unit> =
        safeCall { userService.deleteUser() }
}