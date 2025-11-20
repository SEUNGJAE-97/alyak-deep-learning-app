package com.alyak.detector.core.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

private object PreferencesKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val EXPIRES_IN = longPreferencesKey("expires_in")
    val USER_ID = longPreferencesKey("user_id")
    val TOKEN_SAVED_AT = longPreferencesKey("token_saved_at")
}

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    /**
     * 토큰 저장
     * @param accessToken JWT 액세스 토큰
     * @param expiresIn 토큰 만료 시간 (초)
     * @param userId 사용자 ID
     */
    suspend fun saveToken(accessToken: String, expiresIn: Long, userId: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.EXPIRES_IN] = expiresIn
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.TOKEN_SAVED_AT] = System.currentTimeMillis()
        }
    }

    /**
     * 토큰 저장 (임시 로그인용 - TempLoginResponse)
     * @param tempLoginResponse 임시 로그인 응답
     * @param expiresIn 토큰 만료 시간 (초), 기본값 30일 (2592000초)
     * @param userId 사용자 ID, 기본값 1L
     */
    suspend fun saveToken(
        tempLoginResponse: TempLoginResponse,
        expiresIn: Long = 2592000L, // 기본값: 30일 (3600초 * 24시간 * 30일)
        userId: Long = 1L // 기본값: 임시 사용자 ID
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = tempLoginResponse.accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = tempLoginResponse.refreshToken
            preferences[PreferencesKeys.EXPIRES_IN] = expiresIn
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.TOKEN_SAVED_AT] = System.currentTimeMillis()
        }
    }

    /**
     * 액세스 토큰 가져오기
     * @return 액세스 토큰, 없으면 null
     */
    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
    }

    /**
     * 액세스 토큰 Flow
     * 토큰 변경을 관찰할 수 있음
     */
    val accessTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACCESS_TOKEN]
    }

    /**
     * 사용자 ID 가져오기
     * @return 사용자 ID, 없으면 null
     */
    suspend fun getUserId(): Long? {
        return dataStore.data.first()[PreferencesKeys.USER_ID]
    }

    /**
     * 토큰이 유효한지 확인 (만료 시간 체크)
     * @return 토큰이 있고 만료되지 않았으면 true
     */
    suspend fun isTokenValid(): Boolean {
        val token = getAccessToken() ?: return false
        if (token.isEmpty()) return false

        val expiresIn = dataStore.data.first()[PreferencesKeys.EXPIRES_IN] ?: return false
        val savedAt = dataStore.data.first()[PreferencesKeys.TOKEN_SAVED_AT] ?: return false

        // 현재 시간이 만료 시간을 넘었는지 확인
        val currentTime = System.currentTimeMillis()
        val expirationTime = savedAt + (expiresIn * 1000) // expiresIn은 초 단위

        return currentTime < expirationTime
    }

    /**
     * 모든 토큰 정보 삭제 (로그아웃 시 사용)
     */
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.EXPIRES_IN)
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.TOKEN_SAVED_AT)
        }
    }
}
