package com.example.assistivetouchclone.floating

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.assistivetouchclone.R

class FloatingPopupManager(
    private val context: Context,
    private val floatingManager: FloatingManager
) {

    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var popupView: View? = null

    fun isShowing() = popupView != null

    fun show() {

        if (popupView != null) return

        popupView = LayoutInflater.from(context)
            .inflate(R.layout.layout_popup, null)

        popupView!!.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        val popupWidth = popupView!!.measuredWidth
        val popupHeight = popupView!!.measuredHeight

        val params = WindowManager.LayoutParams(
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

        val screenWidth = context.resources.displayMetrics.widthPixels

        params.x =
            if (floatingManager.params.x > screenWidth / 2)
                floatingManager.params.x - popupWidth
            else
                floatingManager.params.x + floatingManager.floatingView.width

        params.y =
            floatingManager.params.y - popupHeight / 2

        windowManager.addView(
            popupView,
            params
        )

        popupView!!.pivotX =
            if(floatingManager.params.x <
                context.resources.displayMetrics.widthPixels/2)
                0f
            else
                popupView!!.measuredWidth.toFloat()

        popupView!!.pivotY =
            popupView!!.measuredHeight/2f

        popupView!!.scaleX = .2f
        popupView!!.scaleY = .2f
        popupView!!.alpha = 0f

        popupView!!.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(180)
            .start()
    }

    fun hide() {

        popupView?.animate()
            ?.alpha(0f)
            ?.scaleX(.2f)
            ?.scaleY(.2f)
            ?.setDuration(150)
            ?.withEndAction {

                popupView?.let {

                    windowManager.removeView(it)

                }

                popupView = null

            }
            ?.start()

    }

}