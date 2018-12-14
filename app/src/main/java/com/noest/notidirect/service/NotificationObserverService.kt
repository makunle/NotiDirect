package com.noest.notidirect.service

import android.annotation.SuppressLint
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.noest.notidirect.utils.LogX

class NotificationObserverService : NotificationListenerService() {

    val TAG = "NotificationObserverService"

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        LogX.d(TAG, "onNotificationPosted: " + sbn?.packageName)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        LogX.d(TAG, "onNotificationRemoved: " + sbn?.packageName)
    }
}