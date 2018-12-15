package com.noest.notidirect.utils

import android.content.Context
import android.provider.Settings
import com.noest.notidirect.adapter.AppInfo


fun Context.notificationListenerEnable(): Boolean {
    var enable = false
    val packageName = packageName
    val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
    if (flat != null) {
        enable = flat.contains(packageName)
    }
    return enable
}


fun Context.getAppInfo(pkg: String, focus: Boolean): AppInfo? {
    try {
        val pm = packageManager
        val info = pm.getApplicationInfo(pkg, 0)

        val icon = info.loadIcon(pm)
        val name = info.loadLabel(pm).toString()

        return AppInfo(pkg, name, icon, focus)
    } catch (e: Throwable) {
        return null
    }
}
