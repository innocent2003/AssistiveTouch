package com.example.assistivetouchclone

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import androidx.activity.enableEdgeToEdge

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.example.assistivetouchclone.services.FloatingService
import com.example.assistivetouchclone.services.MyAdminReceiver
import com.google.android.material.card.MaterialCardView

class MainActivity : BaseActivity() {
    private val CAMERA_REQUEST = 100
    private lateinit var switchAssistive: Switch
    private lateinit var cardAssistive: MaterialCardView
    private lateinit var txtStatus: TextView

    private lateinit var btnStart: Button
    private lateinit var cardMenu4: MaterialCardView
    private var ignoreSwitchCallback = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isLanguageSelected()) {
            startActivity(Intent(this, LanguageActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)
        switchAssistive = findViewById(R.id.switchAssistive)
        cardAssistive = findViewById(R.id.cardAssistive)
        txtStatus = findViewById(R.id.txtStatus)
        cardMenu4 = findViewById(R.id.cardMenu4)
        val layoutTuyChinh1 = findViewById<LinearLayout>(R.id.layoutTuyChinh1)
        val layoutTuyChinh = findViewById<LinearLayout>(R.id.layoutTuyChinh)
        val layoutCaiDat = findViewById<LinearLayout>(R.id.layoutCaiDat)


        layoutTuyChinh.setOnClickListener {

            val intent = Intent(
                this,
                IconActivity::class.java
            )

            startActivity(intent)
        }

        layoutTuyChinh1.setOnClickListener {

            val intent = Intent(
                this,
                MenuActivity::class.java
            )

            startActivity(intent)
        }

        layoutCaiDat.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val txtTitle3 = findViewById<TextView>(R.id.txtTitle3)
        txtTitle3.setOnClickListener {
            startActivity(Intent(this, SystemActionActivity::class.java))
        }

        cardMenu4.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        initializeSwitchState()

        btnStart.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }
        switchAssistive.setOnCheckedChangeListener { _, isChecked ->
            if (ignoreSwitchCallback) return@setOnCheckedChangeListener

            updateUI(isChecked)

            val serviceIntent = Intent(this, FloatingService::class.java)

            if (isChecked) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (!Settings.canDrawOverlays(this)) {

                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName")
                            )
                        )

                        switchAssistive.isChecked = false
                        return@setOnCheckedChangeListener
                    }
                }

                requestDeviceAdmin()
                requestCameraPermission()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(serviceIntent)
                else
                    startService(serviceIntent)

            } else {

                stopService(serviceIntent)
            }
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

    private fun initializeSwitchState() {
        ignoreSwitchCallback = true
        val isFloatingRunning = FloatingService.isRunning
        switchAssistive.isChecked = isFloatingRunning
        updateUI(isFloatingRunning)
        ignoreSwitchCallback = false
    }

    private fun updateUI(enable: Boolean) {

        if (enable) {

            cardAssistive.setCardBackgroundColor(Color.parseColor("#4285F4"))

            txtStatus.text = getString(R.string.status_on)

            btnStart.text = getString(R.string.start_floating_button)

        } else {

            cardAssistive.setCardBackgroundColor(Color.parseColor("#9E9E9E"))

            txtStatus.text = getString(R.string.status_off)

            btnStart.text = getString(R.string.stop_floating_button)
        }

    }

    private fun isLanguageSelected(): Boolean {
        val preferences = getSharedPreferences("AssistiveSettings", MODE_PRIVATE)
        return preferences.contains("selected_language")
    }




}