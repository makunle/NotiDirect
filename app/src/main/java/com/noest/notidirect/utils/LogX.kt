package com.noest.notidirect.utils

import android.util.Log

object LogX {
    val PREFIX = "notidirect_"
    fun d(tag: String, msg: String?) {
        Log.d(PREFIX + tag, msg)
    }
}