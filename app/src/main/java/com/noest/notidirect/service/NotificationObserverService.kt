package com.noest.notidirect.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.support.v4.app.NotificationCompat
import com.noest.notidirect.R
import com.noest.notidirect.activity.MainActivity
import com.noest.notidirect.control.DirectJumpControl
import com.noest.notidirect.receiver.UnlockReceiver
import com.noest.notidirect.utils.LogX

class NotificationObserverService : NotificationListenerService() {

    val TAG = "NotificationObserverService"

    val jumpPkgFilter = listOf(
        "com.tencent.mobileqq",
        "com.tencent.mm",
        "com.tencent.tim"
    )

    override fun onCreate() {
        super.onCreate()
        start()
    }

    fun filter(pkg: String): Boolean {
        return jumpPkgFilter.contains(pkg)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        LogX.d(TAG, "onNotificationPosted: " + sbn?.packageName)

        if (!filter(sbn?.packageName.toString())) {
            return
        }

        sbn?.let {
            DirectJumpControl.setPendingIntent(sbn.notification.contentIntent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        LogX.d(TAG, "onNotificationRemoved: " + sbn?.packageName)

        if (!filter(sbn?.packageName.toString())) {
            return
        }

        sbn?.let {
            DirectJumpControl.removePendingPkgToJump()
        }
    }

    fun start() {
        UnlockReceiver.regReceiver(this)

        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val channelId = createNotificationChannel("NotiDirect", getString(R.string.channel_name))

        val builder = NotificationCompat.Builder(this, channelId)
        val notification = builder.setContentIntent(pIntent)
            .setContentTitle("亮屏通知跳转服务")
            .setContentText("有及时消息时，亮屏后直接跳转到相应页面")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_icon))
            .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return channelId
    }

    fun stop() {
        UnlockReceiver.unRegReceiver(this)

        stopForeground(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
}