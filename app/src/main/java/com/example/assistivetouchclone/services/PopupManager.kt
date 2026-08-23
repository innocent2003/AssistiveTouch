package com.example.assistivetouchclone.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.assistivetouchclone.LocaleHelper
import com.example.assistivetouchclone.MainActivity
import com.example.assistivetouchclone.R
import com.example.assistivetouchclone.SystemActionCatalog
import com.example.assistivetouchclone.SystemActionItem
import com.example.assistivetouchclone.utils.SystemAction
import com.example.assistivetouchclone.AppInfo
import com.example.assistivetouchclone.utils.ShortcutUtils
import android.content.pm.ResolveInfo
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator

class PopupManager(
    private val service: Service,
    private val windowManager: WindowManager,
    private val floatingManager: FloatingViewManager
) {
    private companion object {
        const val ACTION_SLOT_COUNT = 9
        const val CENTER_SLOT_INDEX = 4
    }

    private var favouritePopup: View? = null
    private var settingPopup: View? = null
    private var popupView: View? = null
    private var dimView: View? = null

    private val favouritePrefs by lazy {
        service.getSharedPreferences("AssistiveSettings", Context.MODE_PRIVATE)
    }

    var isPopupShowing = false
    private var isTransitioning = false

    private fun getLocalizedServiceContext() = LocaleHelper.setLocale(
        service,
        LocaleHelper.getPersistedLanguage(service)
    )

    fun showPopup() {
        if (isTransitioning || isPopupShowing) return

        isTransitioning = true
        hideAllPopup(showFloatingIcon = false, animate = false)
        showDimView()

        val localizedContext = getLocalizedServiceContext()
        popupView = LayoutInflater.from(localizedContext).inflate(R.layout.layout_popup, null)
        applySelectedPopupColor(popupView!!)

        val popupParams = OverlayUtils.createCenteredOverlayLayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        windowManager.addView(popupView, popupParams)
        floatingManager.applySelectedIcon()
        floatingManager.hideFloatingIcon()
        animatePopupIn(popupView) {
            isTransitioning = false
            isPopupShowing = true
        }

        populateActionGrid(
            popupView!!,
            pageIndex = 0,
            backAction = null
        )
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
        showDimView()

        val localizedContext = getLocalizedServiceContext()
        settingPopup = LayoutInflater.from(localizedContext).inflate(R.layout.layout_setting_popup, null)
        applySelectedPopupColor(settingPopup!!)

        val lp = OverlayUtils.createCenteredOverlayLayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        windowManager.addView(settingPopup, lp)
        floatingManager.applySelectedIcon()
        floatingManager.hideFloatingIcon()
        animatePopupIn(settingPopup) {
            isTransitioning = false
            isPopupShowing = true
        }

        populateActionGrid(
            settingPopup!!,
            pageIndex = 1,
            backAction = {
                hideAllPopup()
                showPopup()
            }
        )
    }

    private fun populateActionGrid(
        popup: View,
        pageIndex: Int,
        backAction: (() -> Unit)?
    ) {
        val grid = popup.findViewById<GridLayout>(R.id.actionGrid) ?: return
        val actions = SystemActionCatalog.getSystemActions()
        val preferences = service.getSharedPreferences("AssistiveSettings", Context.MODE_PRIVATE)

        grid.removeAllViews()
        grid.columnCount = 3

        repeat(ACTION_SLOT_COUNT) { slotIndex ->
            if (pageIndex == 1 && slotIndex == CENTER_SLOT_INDEX) {
                grid.addView(createPopupButton(
                    R.drawable.arrow_left_alt_24px,
                    "",
                    backAction ?: {}
                ))
                return@repeat
            }

            val actionIndex = preferences.getInt(
                "page${pageIndex + 1}_action_$slotIndex",
                -1
            )
            val action = actions.getOrNull(actionIndex)
            grid.addView(createActionSlotView(action, slotIndex))
        }
    }

    private fun createActionSlotView(action: SystemActionItem?, slotIndex: Int): View {
        val item = LinearLayout(service).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = (100 * service.resources.displayMetrics.density).toInt()
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            isClickable = action != null
            isFocusable = action != null
            contentDescription = "Action slot ${slotIndex + 1}"
        }

        val icon = ImageView(service).apply {
            layoutParams = LinearLayout.LayoutParams(
                (38 * service.resources.displayMetrics.density).toInt(),
                (38 * service.resources.displayMetrics.density).toInt()
            )
            setImageResource(action?.iconRes ?: android.R.drawable.ic_input_add)
            setColorFilter(Color.WHITE)
            alpha = if (action == null) 0.7f else 1f
            visibility = if (action == null) View.GONE else View.VISIBLE
        }

        val label = TextView(service).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 8, 0, 0)
            text = action?.label ?: "+"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            maxLines = 2
            visibility = if (action == null) View.GONE else View.VISIBLE
        }

        item.addView(icon)
        item.addView(label)
        item.setOnClickListener { executePopupAction(action) }
        return item
    }

    private fun createPopupButton(iconRes: Int, labelText: String, onClick: () -> Unit): View {
        val item = LinearLayout(service).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = (100 * service.resources.displayMetrics.density).toInt()
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            isClickable = true
            isFocusable = true
        }

        val icon = ImageView(service).apply {
            layoutParams = LinearLayout.LayoutParams(
                (38 * service.resources.displayMetrics.density).toInt(),
                (38 * service.resources.displayMetrics.density).toInt()
            )
            setImageResource(iconRes)
            setColorFilter(Color.WHITE)
        }
        val label = TextView(service).apply {
            text = labelText
            setTextColor(Color.WHITE)
        }

        item.addView(icon)
        item.addView(label)
        item.setOnClickListener { onClick() }
        return item
    }

    private fun executePopupAction(action: SystemActionItem?) {
        when (action?.label) {
            "Setting" -> SystemAction.openSettings { showSettingPopup() }
            "Favourite" -> SystemAction.openFavourite { showFavouritePopup() }
            else -> action?.action?.invoke(service)
        }
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

        val localizedContext = getLocalizedServiceContext()
        favouritePopup = LayoutInflater.from(localizedContext).inflate(R.layout.layout_favourite_popup, null)
        applySelectedPopupColor(favouritePopup!!)

        val lp = OverlayUtils.createCenteredOverlayLayoutParams(
            600,
            WindowManager.LayoutParams.WRAP_CONTENT
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

    private fun createFavouriteBackButton(): View {
        val item = LinearLayout(service).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = (96 * service.resources.displayMetrics.density).toInt()
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            setBackgroundResource(android.R.color.transparent)
            isClickable = true
            isFocusable = true
        }

        val icon = ImageView(service).apply {
            layoutParams = LinearLayout.LayoutParams(
                (40 * service.resources.displayMetrics.density).toInt(),
                (40 * service.resources.displayMetrics.density).toInt()
            )
            setImageResource(R.drawable.arrow_left_alt_24px)
            setColorFilter(android.graphics.Color.WHITE)
        }

        val label = TextView(service).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            text = ""
            textSize = 13f
            gravity = Gravity.CENTER
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        item.addView(icon)
        item.addView(label)

        item.setOnClickListener {
            hideAllPopup()
            showPopup()
        }

        return item
    }

    private fun createFavouriteSlotView(index: Int, appInfo: AppInfo?): View {
        val item = LinearLayout(service).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = (96 * service.resources.displayMetrics.density).toInt()
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            setBackgroundResource(android.R.color.transparent)
            isClickable = true
            isFocusable = true
        }

        val icon = ImageView(service).apply {
            layoutParams = LinearLayout.LayoutParams(
                (40 * service.resources.displayMetrics.density).toInt(),
                (40 * service.resources.displayMetrics.density).toInt()
            )
            setImageDrawable(appInfo?.icon ?: service.getDrawable(android.R.drawable.ic_menu_add))
            alpha = if (appInfo == null) 0.7f else 1f
        }

        val label = TextView(service).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 8, 0, 0)
            text = appInfo?.appName ?: ""
            textSize = 13f
            gravity = Gravity.CENTER
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            alpha = if (appInfo == null) 0.7f else 1f
        }

        item.addView(icon)
        item.addView(label)

        item.setOnClickListener {
            showAppPicker(index)
        }

        item.setOnLongClickListener {
            saveFavouriteApp(index, null)
            icon.setImageResource(android.R.drawable.ic_menu_add)
            label.text = "Nothing set"
            icon.alpha = 0.7f
            label.alpha = 0.7f
            applySelectedState(item, false)
            true
        }

        return item
    }

    private fun applySelectedState(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
        view.setBackgroundColor(if (isSelected) Color.parseColor("#33A5D6A7") else Color.TRANSPARENT)
        view.alpha = if (isSelected) 1f else 0.9f
        if (!isSelected) {
            view.background = null
        }
    }

    private fun createAppPickerItem(appInfo: AppInfo, slotIndex: Int): View {
        val item = LinearLayout(service).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 12, 12, 12)
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            isClickable = true
            isFocusable = true
        }

        val icon = ImageView(service).apply {
            layoutParams = LinearLayout.LayoutParams(
                (36 * service.resources.displayMetrics.density).toInt(),
                (36 * service.resources.displayMetrics.density).toInt()
            )
            setImageDrawable(appInfo.icon)
        }

        val label = TextView(service).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(12, 0, 0, 0)
            text = appInfo.appName
            textSize = 15f
            setTextColor(android.graphics.Color.WHITE)
        }

        item.addView(icon)
        item.addView(label)

        item.setOnLongClickListener {
            applySelectedState(item, true)
            saveFavouriteApp(slotIndex, appInfo.packageName)
            true
        }

        item.setOnClickListener {
            applySelectedState(item, true)
            saveFavouriteApp(slotIndex, appInfo.packageName)
            launchApp(appInfo.packageName)
            hideAllPopup()
        }

        return item
    }

    private fun showAppPicker(slotIndex: Int) {
        val root = favouritePopup ?: return
        val container = root.findViewById<LinearLayout>(R.id.favoriteContentContainer) ?: return
        container.removeAllViews()

        val title = TextView(service).apply {
            text = "Choose app"
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }

        val backButton = Button(service).apply {
            text = "Back"
            setOnClickListener { populateFavouriteList() }
        }

        val scrollView = ScrollView(service).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
        }

        val appList = LinearLayout(service).apply {
            orientation = LinearLayout.VERTICAL
        }

        val pm = service.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val apps = pm.queryIntentActivities(intent, 0)
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        apps.forEach { resolveInfo ->
            val label = resolveInfo.loadLabel(pm).toString()
            val pkg = resolveInfo.activityInfo.packageName
            val icon = resolveInfo.loadIcon(pm)
            appList.addView(createAppPickerItem(AppInfo(label, pkg, icon), slotIndex))
        }

        scrollView.addView(appList)
        container.addView(title)
        container.addView(scrollView)
        container.addView(backButton)
    }

    private fun saveFavouriteApp(slotIndex: Int, packageName: String?) {
        val editor = favouritePrefs.edit()
        if (packageName == null) {
            editor.remove("fav_slot_$slotIndex")
        } else {
            editor.putString("fav_slot_$slotIndex", packageName)
        }
        editor.apply()
    }

    private fun applySelectedPopupColor(popup: View) {
        val selectedColor = favouritePrefs.getInt("background_color", Color.BLACK)
        val cornerRadius = 28f * service.resources.displayMetrics.density
        popup.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(selectedColor)
            setCornerRadius(cornerRadius)
        }
    }

    private fun loadFavouriteApp(slotIndex: Int): AppInfo? {
        val packageName = favouritePrefs.getString("fav_slot_$slotIndex", null) ?: return null
        return try {
            val pm = service.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            AppInfo(
                pm.getApplicationLabel(appInfo).toString(),
                packageName,
                pm.getApplicationIcon(appInfo)
            )
        } catch (_: Throwable) {
            null
        }
    }

    private fun launchApp(packageName: String) {
        val launchIntent = service.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            service.startActivity(launchIntent)
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

    private fun populateFavouriteList() {
        val root = favouritePopup ?: return
        val container = root.findViewById<LinearLayout>(R.id.favoriteContentContainer) ?: return
        container.removeAllViews()

        val grid = GridLayout(service).apply {
            columnCount = 3
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        repeat(4) { index ->
            grid.addView(createFavouriteSlotView(index, loadFavouriteApp(index)))
        }

        grid.addView(createFavouriteBackButton())

        repeat(4) { index ->
            val slotIndex = index + 4
            grid.addView(createFavouriteSlotView(slotIndex, loadFavouriteApp(slotIndex)))
        }

        container.addView(grid)
    }
}
