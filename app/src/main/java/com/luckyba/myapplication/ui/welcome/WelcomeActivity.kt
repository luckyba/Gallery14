package com.luckyba.myapplication.ui.welcome

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.luckyba.myapplication.MainActivity
import com.luckyba.myapplication.R
import com.luckyba.myapplication.common.BaseActivity
import com.luckyba.myapplication.databinding.ActivityWelcomeBinding
import com.luckyba.myapplication.util.PermissionUtils
import com.luckyba.myapplication.util.StringUtils.showToast

class WelcomeActivity: BaseActivity("WelcomeActivity") {
    private val EXTERNAL_STORAGE_PERMISSIONS = 12

    lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
    }

    private fun requestPermission() {
        when {
            PermissionUtils.isStoragePermissionsGranted(this) -> start()
            shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE) ->{
//                showToast(this, " Storage permission required ")
                PermissionUtils.requestPermissions(
                    this,
                    EXTERNAL_STORAGE_PERMISSIONS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            }
            else -> {
//                showToast(this, " Storage permission not available ")
                              PermissionUtils.requestPermissions(
                    this,
                    EXTERNAL_STORAGE_PERMISSIONS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            start()
        }
    }

    fun onclickRequestPermission(view: View) {
        PermissionUtils.goToAppSettings(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            EXTERNAL_STORAGE_PERMISSIONS -> {
                var gotPermission = grantResults.isNotEmpty()

                for (result in grantResults) {
                    gotPermission = gotPermission and (result == PackageManager.PERMISSION_GRANTED)
                }

                if (gotPermission) {
//                    showToast(this, " Storage permission available ")
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R ||Environment.isExternalStorageManager()) {
                        start()
                    } else {
                        createView()
                    }
                } else {
                    showToast(this, "Permission denied ")
                    createView()
                }
            } else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun createView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binding.animate = AnimationUtils.loadAnimation(this, R.anim.scale)

    }

    private fun start() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}