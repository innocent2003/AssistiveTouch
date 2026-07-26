package com.example.assistivetouchclone.services

import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.example.assistivetouchclone.R
import com.example.assistivetouchclone.utils.SystemAction

class PopupManager(
    private val service: Service,
    private val windowManager: WindowManager,
    private val floatingManager: FloatingViewManager
) {
    private var favouritePopup: View? = null
    private var settingPopup: View? = null
    private var popupView: View? = null
    private var dimView: View? = null

    var isPopupShowing = false

    fun showPopup() {
        hideAllPopup()
        showDimView()

        popupView = LayoutInflater.from(service).inflate(R.layout.layout_popup, null)

        val popupParams = OverlayUtils.createOverlayLayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT,
            floatingManager.params.x + 80,
            floatingManager.params.y
        )

        windowManager.addView(popupView, popupParams)
        isPopupShowing = true

        val btnHome = popupView!!.findViewById<LinearLayout>(R.id.btnHome)
        val btnSetting = popupView!!.findViewById<LinearLayout>(R.id.btnSetting)
        val btnLock = popupView!!.findViewById<LinearLayout>(R.id.btnLock)
        val btnFavourite = popupView!!.findViewById<LinearLayout>(R.id.btnFavourite)

        btnHome.setOnClickListener {
            goHome()
            hidePopup()
        }

        btnSetting.setOnClickListener { showSettingPopup() }

        btnLock.setOnClickListener {
            lockScreen()
            hidePopup()
        }

        btnFavourite.setOnClickListener {
            showFavouritePopup()
        }
    }

    fun hidePopup() {
        OverlayUtils.removeViewIfAttached(windowManager, popupView)
        popupView = null
        isPopupShowing = false
    }

    fun showSettingPopup() {
        hidePopup()

        settingPopup = LayoutInflater.from(service).inflate(R.layout.layout_setting_popup, null)

        val lp = WindowManager.LayoutParams().apply {
            copyFrom(floatingManager.params)
            width = 500
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        lp.gravity = Gravity.TOP or Gravity.START
        lp.x = floatingManager.params.x + 80
        lp.y = floatingManager.params.y

        windowManager.addView(settingPopup, lp)

        val btnBack = settingPopup!!.findViewById<LinearLayout>(R.id.btnBackSetting)
        val btnWifi = settingPopup!!.findViewById<LinearLayout>(R.id.btnWifi)
        val btnBluetooth = settingPopup!!.findViewById<LinearLayout>(R.id.btnBluetooth)
        val btnRotate = settingPopup!!.findViewById<LinearLayout>(R.id.btnRotate)
        val btnLocation = settingPopup!!.findViewById<LinearLayout>(R.id.btnLocation)
        val btnVolumeUp = settingPopup!!.findViewById<LinearLayout>(R.id.btnVolumeUp)
        val btnVolumeDown = settingPopup!!.findViewById<LinearLayout>(R.id.btnVolumeDown)
        val btnSilent = settingPopup!!.findViewById<LinearLayout>(R.id.btnSilent)
        val btnFlash = settingPopup!!.findViewById<LinearLayout>(R.id.btnFlash)

        btnBack.setOnClickListener {
            hideAllPopup()
            showPopup()
        }

        btnWifi.setOnClickListener { SystemAction.openWifi(service) }
        btnBluetooth.setOnClickListener { SystemAction.openBluetooth(service) }
        btnRotate.setOnClickListener { SystemAction.openDisplay(service) }

        btnLocation.setOnClickListener {
            service.startActivity(
                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        btnVolumeUp.setOnClickListener { SystemAction.volumeUp(service) }
        btnVolumeDown.setOnClickListener { SystemAction.volumeDown(service) }
        btnSilent.setOnClickListener { SystemAction.toggleSilent(service) }
        btnFlash.setOnClickListener { SystemAction.toggleFlash(service) }
    }

    fun lockScreen() {
        val dpm = service.getSystemService(Service.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val component = ComponentName(service, MyAdminReceiver::class.java)
        if (dpm.isAdminActive(component)) dpm.lockNow()
    }

    private fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        service.startActivity(intent)
    }

    fun showDimView() {
        if (dimView != null) return
        dimView = View(service)

        val lp = OverlayUtils.createOverlayLayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            0,
            0
        )

        dimView!!.setOnTouchListener { _, _ ->
            hideAllPopup()
            true
        }

        windowManager.addView(dimView, lp)
    }

    fun hideAllPopup() {
        OverlayUtils.removeViewIfAttached(windowManager, popupView)
        popupView = null

        OverlayUtils.removeViewIfAttached(windowManager, settingPopup)
        settingPopup = null

        OverlayUtils.removeViewIfAttached(windowManager, dimView)
        dimView = null

        isPopupShowing = false
    }

    fun showFavouritePopup() {
        hideAllPopup()
        showDimView()

        favouritePopup = LayoutInflater.from(service).inflate(R.layout.layout_favourite_popup, null)

        val lp = OverlayUtils.createOverlayLayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT,
            floatingManager.params.x + 80,
            floatingManager.params.y
        )

        windowManager.addView(favouritePopup, lp)
    }
}
