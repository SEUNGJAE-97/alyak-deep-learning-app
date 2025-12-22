package com.alyak.detector

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.core.util.PermissionManager
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import com.alyak.detector.navigation.Navigator
import com.alyak.detector.ui.theme.AlyakTheme
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var permissionManager: PermissionManager
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAuthIntent(intent)
        // 권한 요청
        permissionManager = PermissionManager(this)
        // hash key 확인
        var keyHash = Utility.getKeyHash(this)
        // fcm token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result

            Log.d(TAG, "FCM token: $token")
            Toast.makeText(this, "FCM token: $token", Toast.LENGTH_SHORT).show()
        }
        Log.d("Mykey", keyHash)
        setContent {
            AlyakTheme {
                Navigator(permissionManager = permissionManager, tokenManager = tokenManager)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthIntent(intent)
    }

    private fun handleAuthIntent(intent: Intent?) {
        Log.d("Auth", "DeepLink Received: ${intent?.data}")

        intent?.data?.let { uri ->
            if (uri.scheme == "alyak" && uri.host == "auth") {
                val accessToken = uri.getQueryParameter("accessToken")
                val refreshToken = uri.getQueryParameter("refreshToken")
                val email = uri.getQueryParameter("email")

                if (accessToken != null && refreshToken != null && email != null) {
                    // 비동기로 토큰 저장
                    lifecycleScope.launch {
                        // 기존에 만들어두신 saveToken 메서드 활용
                        tokenManager.saveToken(
                            TempLoginResponse(accessToken, refreshToken, email),
                            expiresIn = 2592000L,
                            userId = 1L
                        )
                        email?.let { tokenManager.saveUserInfo(it) }

                        Toast.makeText(this@MainActivity, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}