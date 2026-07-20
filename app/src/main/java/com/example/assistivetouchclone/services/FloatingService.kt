package com.example.assistivetouchclone.services

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.assistivetouchclone.R
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import com.example.assistivetouchclone.utils.SystemAction
import kotlin.math.abs

class FloatingService : Service() {
    private var dimView: View? = null
    private var settingPopup: View? = null
    private var popupView: View? = null

    private var isPopupShowing = false
    private var initialX = 0
    private var initialY = 0

    private var initialTouchX = 0f
    private var initialTouchY = 0f

    private var isDragging = false

    private lateinit var windowManager: WindowManager

    private lateinit var floatingView: View

    private lateinit var params: WindowManager.LayoutParams

    companion object {

        private const val CHANNEL_ID = "floating_channel"

        private const val CHANNEL_NAME = "Floating Button"

    }

    override fun onCreate() {
        super.onCreate()

        createNotification()

        initFloatingButton()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {

        super.onDestroy()

        if (::floatingView.isInitialized) {

            windowManager.removeView(floatingView)

        }

    }

    /**
     * Tạo icon nổi
     */
    private fun initFloatingButton() {

        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

        floatingView =
            LayoutInflater.from(this)
                .inflate(R.layout.layout_floating, null)

        params =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                PixelFormat.TRANSLUCENT
            )

        params.gravity = Gravity.TOP or Gravity.START

        params.x = 100

        params.y = 300

        windowManager.addView(
            floatingView,
            params
        )
        setupTouchListener()
        floatingView.setOnClickListener {

            if (isPopupShowing) {

                hidePopup()

            } else {

                showPopup()

            }

        }

    }

    /**
     * Foreground Notification
     */
    @SuppressLint("ForegroundServiceType")
    private fun createNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)

        }

        val notification: Notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Floating Button")
                    .setContentText("Running...")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build()

            } else {

                Notification.Builder(this)
                    .setContentTitle("Floating Button")
                    .setContentText("Running...")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build()

            }

        startForeground(
            1,
            notification
        )

    }
    private fun setupTouchListener() {

        floatingView.setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    initialX = params.x
                    initialY = params.y

                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    isDragging = false

                    true
                }

                MotionEvent.ACTION_MOVE -> {

                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()

                    if (kotlin.math.abs(dx) > 10 ||
                        kotlin.math.abs(dy) > 10
                    ) {
                        isDragging = true
                    }

                    params.x = initialX + dx
                    params.y = initialY + dy

                    windowManager.updateViewLayout(
                        floatingView,
                        params
                    )

                    true
                }

                MotionEvent.ACTION_UP -> {

                    if (!isDragging) {

                        floatingView.performClick()

                    } else {

                        snapToEdge()

                    }

                    true
                }

                else -> false
            }

        }

    }
    private fun showPopup() {
        hideAllPopup()
        showDimView()

        popupView =
            LayoutInflater.from(this)
                .inflate(R.layout.layout_popup, null)

        val popupParams =
            WindowManager.LayoutParams(

                600,

                WindowManager.LayoutParams.WRAP_CONTENT,

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                PixelFormat.TRANSLUCENT

            )

        popupParams.gravity = Gravity.TOP or Gravity.START

        popupParams.x = params.x + 80

        popupParams.y = params.y

        windowManager.addView(
            popupView,
            popupParams
        )

        isPopupShowing = true



//        popupView!!
//            .findViewById<Button>(R.id.btnClose)
//            .setOnClickListener {
//
//                hidePopup()
//
//            }



//        popupView!!
//            .findViewById<Button>(R.id.btnHome)
//            .setOnClickListener {
//
//                goHome()
//
//                hidePopup()
//
//            }

//        popupView!!
//            .findViewById<LinearLayout>(R.id.btnHome)

//        popupView!!
//            .findViewById<Button>(R.id.btnSetting)
//            .setOnClickListener {
//                android.util.Log.d("POPUP", "Setting clicked")
//                showSettingPopup()
//
//            }
//        popupView!!
//            .findViewById<Button>(R.id.btnLock)
//            .setOnClickListener {
//
//                lockScreen()
//
//                hidePopup()
//
//            }
//        popupView!!
//            .findViewById<LinearLayout>(R.id.btnSetting)
//
//        popupView!!
//            .findViewById<LinearLayout>(R.id.btnLock)
//
//        popupView!!
//            .findViewById<LinearLayout>(R.id.btnFavourite)
//
//        popupView!!
//            .findViewById<LinearLayout>(R.id.btnScreen)

        val btnHome = popupView!!.findViewById<LinearLayout>(R.id.btnHome)
        val btnSetting = popupView!!.findViewById<LinearLayout>(R.id.btnSetting)
        val btnLock = popupView!!.findViewById<LinearLayout>(R.id.btnLock)
        val btnFavourite = popupView!!.findViewById<LinearLayout>(R.id.btnFavourite)
        val btnScreen = popupView!!.findViewById<LinearLayout>(R.id.btnScreen)

        btnHome.setOnClickListener {
            goHome()
            hidePopup()
        }

        btnSetting.setOnClickListener {
            showSettingPopup()
        }

        btnLock.setOnClickListener {
            lockScreen()
            hidePopup()
        }
    }
    private fun hidePopup() {
        popupView?.let {
            if (it.parent != null) {
                windowManager.removeView(it)
            }
        }

        popupView = null
        isPopupShowing = false
    }
    private fun snapToEdge() {

        val displayMetrics = resources.displayMetrics

        val screenWidth = displayMetrics.widthPixels

        val targetX = if (params.x < screenWidth / 2) {
            0
        } else {
            screenWidth - floatingView.width
        }

        ValueAnimator.ofInt(params.x, targetX).apply {

            duration = 250

            addUpdateListener {

                params.x = it.animatedValue as Int

                windowManager.updateViewLayout(
                    floatingView,
                    params
                )
            }

            start()
        }
    }

    private fun goHome() {

        val intent = Intent(Intent.ACTION_MAIN)

        intent.addCategory(Intent.CATEGORY_HOME)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
    }
    private fun showSettingPopup() {
        android.util.Log.d("POPUP", "showSettingPopup")
        hidePopup()

        settingPopup = LayoutInflater.from(this)
            .inflate(R.layout.layout_setting_popup, null)

        val lp = WindowManager.LayoutParams().apply {
            copyFrom(params)

            width = 500
            height = WindowManager.LayoutParams.WRAP_CONTENT

            x = 100
            y = 200
        }

        lp.gravity = Gravity.TOP or Gravity.START

        lp.x = params.x + 80
        lp.y = params.y

        windowManager.addView(settingPopup, lp)

        settingPopup!!
            .findViewById<Button>(R.id.btnCloseSetting)
            .setOnClickListener {

                windowManager.removeView(settingPopup)

                settingPopup = null

            }
        settingPopup!!
            .findViewById<Button>(R.id.btnBack)
            .setOnClickListener {

                if (settingPopup?.parent != null) {
                    windowManager.removeView(settingPopup)
                }

                settingPopup = null

                showPopup()

            }
        settingPopup!!
            .findViewById<Button>(R.id.btnWifi)
            .setOnClickListener {

                SystemAction.openWifi(this)

            }

        settingPopup!!
            .findViewById<Button>(R.id.btnBluetooth)
            .setOnClickListener {

                SystemAction.openBluetooth(this)

            }

        settingPopup!!
            .findViewById<Button>(R.id.btnVolumeUp)
            .setOnClickListener {

                SystemAction.volumeUp(this)

            }

        settingPopup!!
            .findViewById<Button>(R.id.btnVolumeDown)
            .setOnClickListener {

                SystemAction.volumeDown(this)

            }

        settingPopup!!
            .findViewById<Button>(R.id.btnSilent)
            .setOnClickListener {

                SystemAction.toggleSilent(this)

            }

        settingPopup!!
            .findViewById<Button>(R.id.btnRotate)
            .setOnClickListener {

                SystemAction.openDisplay(this)

            }

    }
    private fun lockScreen() {

        val dpm =
            getSystemService(DEVICE_POLICY_SERVICE)
                    as DevicePolicyManager

        val component =
            ComponentName(
                this,
                MyAdminReceiver::class.java
            )

        if (dpm.isAdminActive(component)) {

            dpm.lockNow()

        }

    }
    private fun showDimView() {

        if (dimView != null) return

        dimView = View(this)

        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        lp.gravity = Gravity.TOP or Gravity.START

        dimView!!.setOnTouchListener { _, _ ->

            hideAllPopup()

            true
        }

        windowManager.addView(dimView, lp)
    }
    private fun hideAllPopup() {

        popupView?.let {
            if (it.parent != null)
                windowManager.removeView(it)
        }
        popupView = null

        settingPopup?.let {
            if (it.parent != null)
                windowManager.removeView(it)
        }
        settingPopup = null

        dimView?.let {
            if (it.parent != null)
                windowManager.removeView(it)
        }
        dimView = null

        isPopupShowing = false
    }


}