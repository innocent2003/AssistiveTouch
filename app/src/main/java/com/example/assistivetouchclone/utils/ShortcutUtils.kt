package com.example.assistivetouchclone.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import com.example.assistivetouchclone.AppInfo

object ShortcutUtils {
    fun createPinnedShortcut(context: Context, appInfo: AppInfo) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName) ?: Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            `package` = appInfo.packageName
        }

        val iconBitmap = drawableToBitmap(appInfo.icon)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            val shortcut = ShortcutInfo.Builder(context, appInfo.packageName)
                .setShortLabel(appInfo.appName)
                .setLongLabel(appInfo.appName)
                .setIcon(Icon.createWithBitmap(iconBitmap))
                .setIntent(launchIntent)
                .build()

            if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported) {
                shortcutManager.requestPinShortcut(shortcut, null)
            } else {
                sendLegacyInstallShortcut(context, appInfo.appName, launchIntent, iconBitmap)
            }
        } else {
            sendLegacyInstallShortcut(context, appInfo.appName, launchIntent, iconBitmap)
        }
    }

    private fun sendLegacyInstallShortcut(context: Context, name: String, intent: Intent, icon: Bitmap) {
        val shortcutIntent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
        shortcutIntent.putExtra("duplicate", false)
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon)
        context.sendBroadcast(shortcutIntent)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            drawable.bitmap?.let { return it }
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
