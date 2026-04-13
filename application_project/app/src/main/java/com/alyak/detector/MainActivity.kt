package com.alyak.detector

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.core.util.PermissionManager
import com.alyak.detector.feature.notification.InAppPushNotifier
import com.alyak.detector.feature.notification.ui.InAppPushBannerHost
import com.alyak.detector.feature.notification.data.DeviceTokenRegistrar
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import com.alyak.detector.navigation.Navigator
import com.alyak.detector.ui.theme.AlyakTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var deviceTokenRegistrar: DeviceTokenRegistrar

    @Inject
    lateinit var inAppPushNotifier: InAppPushNotifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAuthIntent(intent)
        // 권한 요청
        permissionManager = PermissionManager(this)
        setContent {
            AlyakTheme {
                InAppPushBannerHost(notifier = inAppPushNotifier) {
                    Navigator(
                        permissionManager = permissionManager,
                        tokenManager = tokenManager,
                    )
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    tokenManager.accessTokenFlow
                        .distinctUntilChanged()
                        .collect { access ->
                            if (!access.isNullOrBlank()) {
                                registerFcmTokenIfLoggedIn()
                            }
                        }
                }
                launch {
                    tokenManager.authEvent.collect { event ->
                        if (event == TokenManager.AuthEvent.LOGOUT) {
                            withContext(Dispatchers.IO) {
                                runCatching { deviceTokenRegistrar.unregister() }
                            }
                            tokenManager.clearToken()
                        }
                    }
                }
            }
        }
    }

    private fun registerFcmTokenIfLoggedIn() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result ?: return@addOnCompleteListener
            lifecycleScope.launch(Dispatchers.IO) {
                if (tokenManager.getAccessToken().isNullOrBlank()) return@launch
                deviceTokenRegistrar.register(token)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthIntent(intent)
    }

    private fun handleAuthIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "alyak" && uri.host == "auth") {
                val accessToken = uri.getQueryParameter("accessToken")
                val refreshToken = uri.getQueryParameter("refreshToken")
                val email = uri.getQueryParameter("email")
                val name = uri.getQueryParameter("userName")
                val userId = uri.getQueryParameter("userId")

                if (accessToken != null && refreshToken != null && email != null && name != null && userId != null) {
                    lifecycleScope.launch {
                        tokenManager.saveToken(
                            TempLoginResponse(accessToken, refreshToken, email),
                            expiresIn = 2592000L,
                            userId = userId.toLong()
                        )
                        email.let { tokenManager.saveUserInfo(email, name) }

                        Toast.makeText(this@MainActivity, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}