package com.example.assistivetouchclone.services

import android.animation.ValueAnimator
import android.app.Service
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import com.example.assistivetouchclone.R

class FloatingViewManager(
    private val service: Service,
    private val windowManager: WindowManager
) {
    lateinit var floatingView: View
    lateinit var params: WindowManager.LayoutParams

    var onClick: () -> Unit = {}

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private lateinit var preferences: SharedPreferences
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "selected_icon") {
            applySelectedIcon()
        }
    }

    fun init() {
        floatingView = LayoutInflater.from(service)
            .inflate(R.layout.layout_floating, null)

        preferences = service.getSharedPreferences("AssistiveSettings", Service.MODE_PRIVATE)
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)

        applySelectedIcon()

        params = OverlayUtils.createOverlayLayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            100,
            300
        )

        windowManager.addView(floatingView, params)

        setupTouchListener()

        floatingView.setOnClickListener {
            if (!isDragging) onClick()
        }
    }

    fun applySelectedIcon() {
        if (!::floatingView.isInitialized) return

        val imgFloatingIcon = floatingView.findViewById<ImageView>(R.id.imgFloat)
        val selectedIcon = preferences.getInt("selected_icon", R.drawable.mood_bad_24px)
        imgFloatingIcon.setImageResource(selectedIcon)
    }

    fun hideFloatingIcon() {
        if (!::floatingView.isInitialized) return

        floatingView.animate().cancel()
        floatingView.animate()
            .alpha(0f)
            .scaleX(0.88f)
            .scaleY(0.88f)
            .setDuration(140)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction { floatingView.visibility = View.INVISIBLE }
            .start()
    }

    fun showFloatingIcon() {
        if (!::floatingView.isInitialized) return

        floatingView.visibility = View.VISIBLE
        floatingView.alpha = 0f
        floatingView.scaleX = 0.88f
        floatingView.scaleY = 0.88f

        floatingView.animate().cancel()
        floatingView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(180)
            .setInterpolator(OvershootInterpolator(0.95f))
            .start()
    }

    fun destroy() {
        if (::floatingView.isInitialized) {
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
            OverlayUtils.removeViewIfAttached(windowManager, floatingView)
        }
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

                    if (kotlin.math.abs(dx) > 10 || kotlin.math.abs(dy) > 10) {
                        isDragging = true
                    }

                    params.x = initialX + dx
                    params.y = initialY + dy

                    windowManager.updateViewLayout(floatingView, params)

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

    fun snapToEdge() {
        val displayMetrics = service.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val targetX = if (params.x < screenWidth / 2) 0 else screenWidth - floatingView.width

        ValueAnimator.ofInt(params.x, targetX).apply {
            duration = 250
            addUpdateListener {
                params.x = it.animatedValue as Int
                windowManager.updateViewLayout(floatingView, params)
            }
            start()
        }
    }
}
