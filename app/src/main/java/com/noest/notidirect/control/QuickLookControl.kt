package com.noest.notidirect.control

import android.service.notification.StatusBarNotification
import com.noest.notidirect.utils.minicache.MiniCache


object QuickLookControl {

    val kv = MiniCache.getCache("quicklook")

    private var mSbnList = LinkedHashMap<String, StatusBarNotification>()

    fun changeFocus(pkg: String, focus: Boolean) {
        kv.put(pkg, focus)
    }

    fun getNotiList(): Map<String, Boolean> {
        val map = kv.allBoolean

        map.optDefalut("com.tencent.mobileqq")
        map.optDefalut("com.tencent.mm")
        map.optDefalut("com.miui.sms")

        return map
    }

    fun MutableMap<String, Boolean>.optDefalut(pkg: String) {
        if (!containsKey(pkg)) {
            put(pkg, false)
        }
    }

    fun filter(pkg: String): Boolean {
        return kv.getBoolean(pkg)
    }


    fun onPresent() {
        mSbnList.forEach {
            val sbn = it.value
            if (filter(sbn.packageName)) {
                sbn.notification.contentIntent.send()
            }
        }
        mSbnList.clear()
    }

    fun addPendingIntent(sbn: StatusBarNotification) {
        mSbnList.put(sbn.packageName + sbn.id, sbn)
        kv.put(sbn.packageName, kv.getBoolean(sbn.packageName))
    }

    fun removePendingPkgToJump(sbn: StatusBarNotification) {
        mSbnList.remove(sbn.packageName + sbn.id)
    }

    fun onLocked() {
    }
}