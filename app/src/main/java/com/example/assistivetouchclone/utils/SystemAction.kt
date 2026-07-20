package com.example.assistivetouchclone.utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings

object SystemAction {

    fun volumeUp(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        am.adjustVolume(
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    fun volumeDown(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        am.adjustVolume(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )
    }

    fun toggleSilent(context: Context) {

        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val nm =
                context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            if (!nm.isNotificationPolicyAccessGranted) {

                val intent =
                    Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                context.startActivity(intent)

                return
            }
        }

        am.ringerMode =
            if (am.ringerMode == AudioManager.RINGER_MODE_NORMAL)
                AudioManager.RINGER_MODE_SILENT
            else
                AudioManager.RINGER_MODE_NORMAL
    }

    fun openWifi(context: Context) {

        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        context.startActivity(intent)
    }

    fun openBluetooth(context: Context) {

        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        context.startActivity(intent)
    }

    fun openDisplay(context: Context) {

        val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        context.startActivity(intent)
    }

}