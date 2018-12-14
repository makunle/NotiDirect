package com.noest.notidirect.control

import android.app.PendingIntent
import android.content.Context


object DirectJumpControl {

    val TAG = "DirectJumpControl"

    var mPendingIntent: PendingIntent? = null

    var mUnlocked = false

    fun performJump(context: Context) {
        mPendingIntent?.send()
    }

    fun setPendingIntent(intent: PendingIntent) {
        if (!mUnlocked) {
            mPendingIntent = intent
        }
    }

    fun removePendingPkgToJump() {
        mPendingIntent = null
    }

    fun onLocked() {
        mUnlocked = false
    }

    fun onUnlocked() {
        mUnlocked = true
    }
}