package com.noest.notidirect.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.CompoundButton
import com.noest.notidirect.R
import com.noest.notidirect.adapter.AppInfo
import com.noest.notidirect.adapter.FocusAppAdapter
import com.noest.notidirect.control.QuickLookControl
import com.noest.notidirect.utils.getAppInfo
import com.noest.notidirect.utils.notificationListenerEnable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "开屏通知跳转"

        if (!notificationListenerEnable()) {
            openNotificationPermissionPage()
            finish()
        }

        val focusInfo = QuickLookControl.getNotiList()

        if (focusInfo.size > 0) {
            val array = ArrayList<AppInfo>(focusInfo.size)
            for ((i, item) in focusInfo.entries.withIndex()) {
                val info = getAppInfo(item.key, item.value)
                if (info != null) {
                    array.add(info)
                }
            }

            val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                val position = buttonView.tag as Int
                QuickLookControl.changeFocus(array[position].pkg, isChecked)
            }

            // list to select
            val adapter = FocusAppAdapter(
                this,
                R.layout.app_item_layout,
                array.toTypedArray(),
                listener
            )
            lvAppList.adapter = adapter
        }
    }

    fun openNotificationPermissionPage() {
        val intent: Intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        } else {
            intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        }
        startActivity(intent)
    }
}
