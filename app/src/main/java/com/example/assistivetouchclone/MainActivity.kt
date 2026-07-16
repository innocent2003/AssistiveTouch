package com.example.assistivetouchclone

import androidx.activity.enableEdgeToEdge

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.assistivetouchclone.services.FloatingService

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)

        btnStart.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!Settings.canDrawOverlays(this)) {

                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )

                    startActivity(intent)

                    return@setOnClickListener
                }
            }

            val serviceIntent = Intent(this, FloatingService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                startForegroundService(serviceIntent)

            } else {

                startService(serviceIntent)

            }

        }

    }

}