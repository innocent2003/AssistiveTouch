package com.example.assistivetouchclone.services

import android.animation.ValueAnimator
import android.app.Service
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

    fun init() {
        floatingView = LayoutInflater.from(service)
            .inflate(R.layout.layout_floating, null)

        val imgFloatingIcon = floatingView.findViewById<ImageView>(R.id.imgFloat)
        val preferences = service.getSharedPreferences("AssistiveSettings", Service.MODE_PRIVATE)
        val selectedIcon = preferences.getInt("selected_icon", R.drawable.mood_bad_24px)
        imgFloatingIcon.setImageResource(selectedIcon)

        params = OverlayUtils.createOverlayLayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            100,
            300
        )

        windowManager.addView(floatingView, params)

        setupTouchListener()

        floatingView.isClickable = true
        floatingView.isFocusable = true
        floatingView.visibility = View.VISIBLE
        floatingView.alpha = 1f
        floatingView.scaleX = 1f
        floatingView.scaleY = 1f

        floatingView.setOnClickListener {
            if (!isDragging) onClick()
        }
    }

    fun destroy() {
        if (::floatingView.isInitialized) {
            OverlayUtils.removeViewIfAttached(windowManager, floatingView)
        }
    }

    fun hideFloatingIcon() {
        if (!::floatingView.isInitialized) return
        floatingView.isClickable = false
        floatingView.isFocusable = false
        floatingView.visibility = View.INVISIBLE
    }

    fun showFloatingIcon() {
        if (!::floatingView.isInitialized) return
        floatingView.isClickable = true
        floatingView.isFocusable = true
        floatingView.visibility = View.VISIBLE
        floatingView.alpha = 1f
        floatingView.scaleX = 1f
        floatingView.scaleY = 1f
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
