package com.example.assistivetouchclone

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import androidx.activity.enableEdgeToEdge

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.assistivetouchclone.services.FloatingService
import com.example.assistivetouchclone.services.MyAdminReceiver

class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST = 100

    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)
//
//        btnStart.setOnClickListener {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                if (!Settings.canDrawOverlays(this)) {
//
//                    val intent = Intent(
//                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:$packageName")
//                    )
//
//                    startActivity(intent)
//
//                    return@setOnClickListener
//                }
//            }
//
//            val serviceIntent = Intent(this, FloatingService::class.java)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                startForegroundService(serviceIntent)
//
//            } else {
//
//                startService(serviceIntent)
//
//            }
//
//        }
        btnStart.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!Settings.canDrawOverlays(this)) {

                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )

                    return@setOnClickListener
                }
            }

            // Xin quyền Device Admin
            requestDeviceAdmin()
            requestCameraPermission()

            val serviceIntent = Intent(this, FloatingService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(serviceIntent)
            else
                startService(serviceIntent)
        }

    }
    private fun requestDeviceAdmin() {

        val component = ComponentName(this, MyAdminReceiver::class.java)

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)

        intent.putExtra(
            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
            component
        )

        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Ứng dụng cần quyền để khóa màn hình."
        )

        startActivity(intent)
    }
    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_REQUEST
                )
            }
        }
    }
}