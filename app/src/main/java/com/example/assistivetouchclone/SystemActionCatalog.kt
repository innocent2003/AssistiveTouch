package com.example.assistivetouchclone

import android.content.Context
import com.example.assistivetouchclone.utils.SystemAction

object SystemActionCatalog {
    fun getSystemActions(): List<SystemActionItem> = listOf(
        SystemActionItem("Open Wifi", R.drawable.wifi_24px) { context -> SystemAction.openWifi(context) },
        SystemActionItem("Open Bluetooth", R.drawable.bluetooth_24px) { context -> SystemAction.openBluetooth(context) },
        SystemActionItem("Open Display", R.drawable.mobile_rotate_lock_24px) { context -> SystemAction.openDisplay(context) },
        SystemActionItem("Volume Up", R.drawable.volume_up_24px) { context -> SystemAction.volumeUp(context) },
        SystemActionItem("Volume Down", R.drawable.volume_down_24px) { context -> SystemAction.volumeDown(context) },
        SystemActionItem("Toggle Silent", R.drawable.notifications_active_24px) { context -> SystemAction.toggleSilent(context) },
        SystemActionItem("Toggle Flash", R.drawable.highlight_24px) { context -> SystemAction.toggleFlash(context) },
        SystemActionItem("Favourite", android.R.drawable.star_big_on) { },
        SystemActionItem("Setting", android.R.drawable.ic_menu_manage) { },
        SystemActionItem("Lock", android.R.drawable.ic_lock_lock) { context -> SystemAction.lockScreen(context) },
        SystemActionItem("Home", android.R.drawable.ic_menu_view) { context -> SystemAction.openHome(context) }
    )
}

data class SystemActionItem(
    val label: String,
    val iconRes: Int,
    val action: (Context) -> Unit
)
