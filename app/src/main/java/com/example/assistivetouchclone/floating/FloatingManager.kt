package com.example.assistivetouchclone.floating

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.assistivetouchclone.R

class FloatingManager(
    private val context: Context
) {

    val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    lateinit var floatingView: View

    lateinit var params: WindowManager.LayoutParams

    fun show() {

        floatingView =
            LayoutInflater.from(context)
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

        params.y = 400

        windowManager.addView(
            floatingView,
            params
        )
    }

    fun remove() {

        if (::floatingView.isInitialized) {

            windowManager.removeView(floatingView)

        }

    }

}