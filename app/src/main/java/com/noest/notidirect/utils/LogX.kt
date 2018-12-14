package com.noest.notidirect.utils

import android.util.Log
import com.noest.notidirect.BuildConfig

object LogX {
    val PREFIX = "notidirect_"
    var mDebugging = BuildConfig.DEBUG

    fun setDebugging(debug: Boolean) {
        mDebugging = debug
    }

    fun d(tag: String, msg: String?) {
        if (mDebugging) {
            Log.d(PREFIX + tag, msg)
        }
    }
}