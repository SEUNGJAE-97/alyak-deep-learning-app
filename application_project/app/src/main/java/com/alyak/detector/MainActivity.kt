package com.alyak.detector

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alyak.detector.core.util.PermissionManager
import com.alyak.detector.navigation.Navigator
import com.alyak.detector.ui.theme.AlyakTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                Navigator(permissionManager = permissionManager)
            }
        }
    }
}