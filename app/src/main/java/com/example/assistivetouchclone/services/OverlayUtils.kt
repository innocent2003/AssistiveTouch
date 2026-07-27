package com.example.assistivetouchclone.services

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager

object OverlayUtils {
    fun createOverlayLayoutParams(
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        flags: Int = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    ): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            width,
            height,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            this.x = x
            this.y = y
        }
    }

    fun removeViewIfAttached(windowManager: WindowManager, view: View?) {
        view?.let {
            if (it.parent != null) windowManager.removeView(it)
        }
    }
}
