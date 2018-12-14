package com.noest.notidirect.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.noest.notidirect.utils.LogX

object UnlockReceiver : BroadcastReceiver() {

    val TAG = "UnlockReceiver"

    fun regReceiver(context: Context) {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(UnlockReceiver, filter)
    }

    fun unRegReceiver(context: Context) {
        context.unregisterReceiver(UnlockReceiver)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        LogX.d(TAG, intent?.action)
    }
}