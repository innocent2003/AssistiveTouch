package com.example.assistivetouchclone.floating

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class FloatingTouchListener(
    private val manager: FloatingManager
) : View.OnTouchListener {

    private var initialX = 0
    private var initialY = 0

    private var touchX = 0f
    private var touchY = 0f

    var onClick: (() -> Unit)? = null

    override fun onTouch(v: View?, event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

                initialX = manager.params.x
                initialY = manager.params.y

                touchX = event.rawX
                touchY = event.rawY

                manager.floatingView.alpha = 1f

                return true
            }

            MotionEvent.ACTION_MOVE -> {

                val dx = (event.rawX - touchX).toInt()
                val dy = (event.rawY - touchY).toInt()

                manager.params.x = initialX + dx
                manager.params.y = initialY + dy

                manager.windowManager.updateViewLayout(
                    manager.floatingView,
                    manager.params
                )

                return true
            }

            MotionEvent.ACTION_UP -> {

                val dx = abs(event.rawX - touchX)

                val dy = abs(event.rawY - touchY)

                if (dx < 10 && dy < 10) {

                    onClick?.invoke()

                }

                snap()

                fade()

                return true
            }

        }

        return false
    }

    private fun snap() {

        val width =
            manager.windowManager.currentWindowMetrics.bounds.width()

        val target =
            if (manager.params.x < width / 2)
                0
            else
                width - manager.floatingView.width

        ValueAnimator.ofInt(
            manager.params.x,
            target
        ).apply {

            duration = 180

            addUpdateListener {

                manager.params.x = it.animatedValue as Int

                manager.windowManager.updateViewLayout(
                    manager.floatingView,
                    manager.params
                )

            }

            start()

        }

    }

    private fun fade() {

        manager.floatingView.animate()
            .alpha(0.45f)
            .setStartDelay(2000)
            .setDuration(300)
            .start()

    }

}