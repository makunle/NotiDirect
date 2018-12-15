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
        if (map.size == 0) {
            map.put("com.tencent.mobileqq", false)
            map.put("com.tencent.mm", false)
            map.put("com.miui.sms", false)
        }
        return map
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