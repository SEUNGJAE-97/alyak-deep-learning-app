package com.alyak.detector.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.Manifest
import androidx.core.content.ContextCompat

/** 권한이 모두 허용되었을 때 실행할 콜백 인터페이스**/
fun interface OnGrantedListener {
    fun onGranted()
}

/**
 * 액티비티 또는 프래그먼트에서 런타임 권한을 요청하고 결과를 처리하는 매니저 클래스
 */
class PermissionManager(
    activityOrFragment: Any
) {
    private lateinit var context: Context

    private lateinit var permitted: OnGrantedListener
    fun setOnGrantedListener(listener: OnGrantedListener) {
        permitted = listener
    }

    /**
     * 전달받은 권한 배열이 모두 허용되어 있는지 확인하는 함수
     * @param context 컨텍스트
     * @param permissions 확인할 권한 배열
     * @return 모든 권한이 허용되어 있으면 true, 아니면 false
     */
    fun checkPermission(context: Context, permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 런타임 권한 요청을 위한 ActivityResultLauncher
     * 권한 요청 결과는 resultChecking()에서 처리
     */
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        when (activityOrFragment) {
            is AppCompatActivity -> {
                activityOrFragment.registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    resultChecking(it)
                }
            }

            is Fragment -> {
                activityOrFragment.registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    resultChecking(it)
                }
            }

            else -> {
                throw RuntimeException("Activity혹은 Fragment에서 권한설정이 가능합니다.")
            }
        }

    /**
     * 권한 요청 결과를 받아 처리하는 함수
     * 하나라도 거부되면 설정 이동 안내, 모두 허용되면 콜백 실행
     * @param result 권한별 허용 여부 맵
     */
    private fun resultChecking(result: Map<String, Boolean>) {

        if (result.values.contains(false)) {
            Toast.makeText(context, "권한이 부족합니다.", Toast.LENGTH_SHORT).show()
            moveToSettings()
        } else {
            Toast.makeText(context, "모든 권한이 허가되었습니다.", Toast.LENGTH_SHORT).show()
            permitted.onGranted()
        }
    }

    /**
     * 사용자가 권한을 거부했을 때, 앱 설정 화면으로 이동하도록 안내하는 함수
     */
    fun moveToSettings() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("권한이 필요합니다.")
        alertDialog.setMessage("설정으로 이동합니다.")
        alertDialog.setPositiveButton("확인") { dialogInterface, i ->
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + context.packageName))
            context.startActivity(intent)
            dialogInterface.cancel()
        }
        alertDialog.setNegativeButton("취소") { dialogInterface, i -> dialogInterface.cancel() }
        alertDialog.show()
    }

    /** 사용자 위치 정보 액세스 권한 요청 **/
    fun requestPermissions(){
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

