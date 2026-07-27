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
import com.example.assistivetouchclone.AppInfo
import com.example.assistivetouchclone.utils.ShortcutUtils
import android.content.pm.ResolveInfo
import android.widget.ImageView
import android.widget.TextView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator

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
    private var isTransitioning = false

    fun showPopup() {
        if (isTransitioning || isPopupShowing) return

        isTransitioning = true
        hideAllPopup(showFloatingIcon = false, animate = false)
        showDimView()

        popupView = LayoutInflater.from(service).inflate(R.layout.layout_popup, null)

        val popupParams = OverlayUtils.createOverlayLayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT,
            floatingManager.params.x + 80,
            floatingManager.params.y
        )

        windowManager.addView(popupView, popupParams)
        floatingManager.applySelectedIcon()
        floatingManager.hideFloatingIcon()
        animatePopupIn(popupView) {
            isTransitioning = false
            isPopupShowing = true
        }

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
        if (isTransitioning) return

        val viewToClose = popupView
        popupView = null
        isTransitioning = true
        isPopupShowing = false
        animatePopupOut(viewToClose) {
            isTransitioning = false
            floatingManager.showFloatingIcon()
        }
    }

    fun showSettingPopup() {
        if (isTransitioning) return

        isTransitioning = true
        hideAllPopup(showFloatingIcon = false, animate = false)

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
        floatingManager.applySelectedIcon()
        floatingManager.hideFloatingIcon()
        animatePopupIn(settingPopup) {
            isTransitioning = false
            isPopupShowing = true
        }

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
            0,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        )

        dimView!!.setOnTouchListener { _, _ ->
            hideAllPopup()
            true
        }

        windowManager.addView(dimView, lp)
    }

    fun hideAllPopup(showFloatingIcon: Boolean = true, animate: Boolean = true) {
        val currentPopup = popupView
        val currentSettingPopup = settingPopup
        val currentFavouritePopup = favouritePopup
        val currentDimView = dimView

        popupView = null
        settingPopup = null
        favouritePopup = null
        dimView = null
        isPopupShowing = false

        if (animate) {
            listOf(currentPopup, currentSettingPopup, currentFavouritePopup).forEach { view ->
                if (view != null) animatePopupOut(view) { OverlayUtils.removeViewIfAttached(windowManager, view) }
            }
        } else {
            listOf(currentPopup, currentSettingPopup, currentFavouritePopup).forEach { view ->
                OverlayUtils.removeViewIfAttached(windowManager, view)
            }
        }

        OverlayUtils.removeViewIfAttached(windowManager, currentDimView)

        if (showFloatingIcon) {
            floatingManager.showFloatingIcon()
        }
        isTransitioning = false
    }

    fun showFavouritePopup() {
        if (isTransitioning) return

        isTransitioning = true
        hideAllPopup(showFloatingIcon = false, animate = false)
        showDimView()

        favouritePopup = LayoutInflater.from(service).inflate(R.layout.layout_favourite_popup, null)

        val lp = OverlayUtils.createOverlayLayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT,
            floatingManager.params.x + 80,
            floatingManager.params.y
        )

        windowManager.addView(favouritePopup, lp)
        floatingManager.applySelectedIcon()
        floatingManager.hideFloatingIcon()
        animatePopupIn(favouritePopup) {
            isTransitioning = false
            isPopupShowing = true
        }

        populateFavouriteList()
    }

    private fun animatePopupIn(view: View?, onComplete: (() -> Unit)? = null) {
        if (view == null) {
            onComplete?.invoke()
            return
        }

        view.alpha = 0f
        view.scaleX = 0.9f
        view.scaleY = 0.9f
        view.translationY = 24f

        view.animate().cancel()
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(220)
            .setInterpolator(OvershootInterpolator(0.95f))
            .withEndAction { onComplete?.invoke() }
            .start()
    }

    private fun animatePopupOut(view: View?, onComplete: (() -> Unit)? = null) {
        if (view == null) {
            onComplete?.invoke()
            return
        }

        view.animate().cancel()
        view.animate()
            .alpha(0f)
            .scaleX(0.94f)
            .scaleY(0.94f)
            .translationY(16f)
            .setDuration(180)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                OverlayUtils.removeViewIfAttached(windowManager, view)
                onComplete?.invoke()
            }
            .start()
    }

    private fun populateFavouriteList() {
        val root = favouritePopup ?: return

        // Try to find a container in the layout by name; fallback to root if not present
        val resId = service.resources.getIdentifier("fav_container", "id", service.packageName)
        val container = if (resId != 0) root.findViewById<ViewGroup>(resId) else (root as? ViewGroup)

        val pm = service.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val apps = pm.queryIntentActivities(intent, 0)

        val parent = container as? ViewGroup ?: return
        parent.removeAllViews()

        val itemPadding = (8 * service.resources.displayMetrics.density).toInt()

        apps.forEach { ri: ResolveInfo ->
            val label = ri.loadLabel(pm).toString()
            val icon = ri.loadIcon(pm)
            val pkg = ri.activityInfo.packageName

            val appInfo = AppInfo(label, pkg, icon)

            val item = LinearLayout(service).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(itemPadding, itemPadding, itemPadding, itemPadding)
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                isClickable = true
                isFocusable = true
            }

            val iv = ImageView(service).apply {
                setImageDrawable(icon)
                val size = (40 * service.resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size)
            }

            val tv = TextView(service).apply {
                text = label
                setPadding(itemPadding, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }

            item.addView(iv)
            item.addView(tv)

            item.setOnClickListener {
                // Launch the selected app
                val launch = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    component = android.content.ComponentName(pkg, ri.activityInfo.name)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    service.startActivity(launch)
                } catch (t: Throwable) {
                    // fallback: try package launch intent
                    val pmLaunch = service.packageManager.getLaunchIntentForPackage(pkg)
                    pmLaunch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    pmLaunch?.let { service.startActivity(it) }
                }
                hideAllPopup()
            }

            item.setOnLongClickListener {
                // Long-press to create a pinned shortcut
                ShortcutUtils.createPinnedShortcut(service, appInfo)
                hideAllPopup()
                true
            }

            parent.addView(item)
        }
    }
}
