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

        map.optDefault("com.tencent.mobileqq")
        map.optDefault("com.tencent.mm")
        map.optDefault("com.miui.sms")
        map.optDefault("com.tencent.tim")
        map.optDefault("com.android.phone")
        map.optDefault("com.android.shell")
        map.optDefault("com.android.providers.userdictionary")
        map.optDefault("com.miui.providers.weather")
        map.optDefault("bubei.tingshu")
        map.optDefault("com.miui.greenguard")
        map.optDefault("com.android.location.fused")
        map.optDefault("com.securespaces.android.agent")
        map.optDefault("com.android.deskclock")
        map.optDefault("com.android.systemui")
        map.optDefault("com.xiaomi.jr.security")
        map.optDefault("com.mimoprint.xiaomi")
        map.optDefault("com.android.bluetoothmidiservice")
        map.optDefault("com.securespaces.android.settings")
        map.optDefault("com.qualcomm.qti.networksetting")
        map.optDefault("com.miui.smsextra")
        map.optDefault("com.android.thememanager")
        map.optDefault("com.qualcomm.fastdormancy")
        map.optDefault("com.android.thememanager.module")
        map.optDefault("com.taobao.taobao")
        map.optDefault("com.xiaomi.mimobile.noti")
        map.optDefault("com.lbe.security.miui")
        map.optDefault("com.miui.whetstone")
        map.optDefault("com.android.bluetooth")
        map.optDefault("com.qualcomm.timeservice")
        map.optDefault("com.qualcomm.embms")
        map.optDefault("com.android.providers.contacts")
        map.optDefault("com.netease.cloudmusic")
        map.optDefault("tv.danmaku.bili")
        map.optDefault("com.android.captiveportallogin")
        map.optDefault("com.eg.android.AlipayGphone")
        map.optDefault("cn.wps.moffice_eng")
        map.optDefault("com.miui.core")
        map.optDefault("com.miui.home")

        return map
    }

    fun MutableMap<String, Boolean>.optDefault(pkg: String) {
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