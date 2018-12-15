package com.noest.notidirect.control

import android.service.notification.StatusBarNotification
import com.noest.notidirect.utils.minicache.MiniCache


object QuickLookControl {

    val kv = MiniCache.getCache("quicklook")

    var mLocked = false

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

        mLocked = false
    }

    fun addPendingIntent(sbn: StatusBarNotification) {
        kv.put(sbn.packageName, kv.getBoolean(sbn.packageName))
        if (mLocked) {
            mSbnList.put(sbn.packageName + sbn.id, sbn)
        }
    }

    fun removePendingPkgToJump(sbn: StatusBarNotification) {
        mSbnList.remove(sbn.packageName + sbn.id)
    }

    fun onLocked() {
        mLocked = true
    }
}