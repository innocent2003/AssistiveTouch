
package com.example.assistivetouchclone.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import com.example.assistivetouchclone.R

class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingManager: FloatingViewManager
    private lateinit var popupManager: PopupManager

    companion object {
        private const val CHANNEL_ID = "floating_channel"
        private const val CHANNEL_NAME = "Floating Button"
        var isRunning = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true

        createNotification()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        floatingManager = FloatingViewManager(this, windowManager)
        floatingManager.init()

        popupManager = PopupManager(this, windowManager, floatingManager)

        floatingManager.onClick = {
            if (popupManager.isPopupShowing) popupManager.hidePopup() else popupManager.showPopup()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        floatingManager.destroy()
        popupManager.hideAllPopup()
    }

    @SuppressLint("ForegroundServiceType")
    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Floating Button")
                .setContentText("Running...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Floating Button")
                .setContentText("Running...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        }

        startForeground(1, notification)
    }
}
