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
import com.example.assistivetouchclone.MainActivity
import com.example.assistivetouchclone.PopupIconSettingsActivity
import com.example.assistivetouchclone.R
import com.example.assistivetouchclone.utils.SystemAction
import com.example.assistivetouchclone.AppInfo
import com.example.assistivetouchclone.utils.ShortcutUtils
import android.content.pm.ResolveInfo
import android.widget.ImageView
import android.widget.TextView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
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
        val btnScreen = popupView!!.findViewById<LinearLayout>(R.id.btnScreen)

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

        btnScreen.setOnClickListener {
            service.startActivity(
                Intent(service, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            hidePopup()
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

        hideAllPopup(showFloatingIcon = false, animate = false)
        service.startActivity(
            Intent(service, PopupIconSettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
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

        // wire up RecyclerView grid and buttons
        val rv = favouritePopup!!.findViewById<RecyclerView>(R.id.rvFavourite)
        val btnAdd = favouritePopup!!.findViewById<Button>(R.id.btnAddApp)
        val btnBack = favouritePopup!!.findViewById<Button>(R.id.btnBack)

        populateFavouriteList(rv)

        btnAdd.setOnClickListener {
            // Open ProductListActivity so user can choose apps
            service.startActivity(
                Intent(service, com.example.assistivetouchclone.ProductListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            hideAllPopup()
        }

        btnBack.setOnClickListener {
            hideAllPopup()
            showPopup()
        }
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

    private fun populateFavouriteList(rv: RecyclerView?) {
        val recycler = rv ?: return

        val pm = service.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val apps = pm.queryIntentActivities(intent, 0)

        recycler.layoutManager = GridLayoutManager(service, 4)
        recycler.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            inner class VH(view: View) : RecyclerView.ViewHolder(view) {
                val icon: ImageView = view.findViewById(R.id.appIcon)
                val name: TextView = view.findViewById(R.id.appName)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val v = LayoutInflater.from(service).inflate(R.layout.item_app, parent, false)
                return VH(v)
            }

            override fun getItemCount(): Int = apps.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val ri = apps[position]
                val pmLabel = ri.loadLabel(pm).toString()
                val pmIcon = ri.loadIcon(pm)
                val pkg = ri.activityInfo.packageName

                val vh = holder as VH
                vh.icon.setImageDrawable(pmIcon)
                vh.name.text = pmLabel

                vh.itemView.setOnClickListener {
                    // Launch chosen app
                    val launch = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_LAUNCHER)
                        component = android.content.ComponentName(pkg, ri.activityInfo.name)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try {
                        service.startActivity(launch)
                    } catch (t: Throwable) {
                        val pmLaunch = service.packageManager.getLaunchIntentForPackage(pkg)
                        pmLaunch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        pmLaunch?.let { service.startActivity(it) }
                    }
                    hideAllPopup()
                }

                vh.itemView.setOnLongClickListener {
                    val appInfo = AppInfo(pmLabel, pkg, pmIcon)
                    ShortcutUtils.createPinnedShortcut(service, appInfo)
                    hideAllPopup()
                    true
                }
            }
        }

    }
}
