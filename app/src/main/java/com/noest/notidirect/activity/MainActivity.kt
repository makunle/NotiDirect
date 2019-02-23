package com.noest.notidirect.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import com.noest.notidirect.R
import com.noest.notidirect.adapter.AppInfo
import com.noest.notidirect.adapter.FocusAppAdapter
import com.noest.notidirect.control.QuickLookControl
import com.noest.notidirect.utils.LogX
import com.noest.notidirect.utils.getAppInfo
import com.noest.notidirect.utils.minicache.MiniCache
import com.noest.notidirect.utils.notificationListenerEnable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    val kv = MiniCache.getCache("quicklook")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = getString(R.string.app_title)

        if (!kv.getBoolean("dont_show_helper")) {
            AlertDialog.Builder(this)
                .setTitle("帮助")
                .setMessage("1、app功能需获取通知权限，点击右上角设置按钮开启通知权限，设备重启后需重新开启\n2、为避免app进程被杀，在当前页面下查看最近任务并锁定该应用\n3、不在列表中的应用在显示通知后会加入到列表中")
                .setPositiveButton("确定") { _, _ ->
                    kv.put("dont_show_helper", true)
                }
                .show()
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

            val listener = View.OnClickListener { view ->
                val cb = view as CheckBox
                val isChecked = cb.isChecked
                val position = view.tag as Int
                array.get(position).focus = isChecked
                QuickLookControl.changeFocus(array[position].pkg, isChecked)
                LogX.d(TAG, "change focus: " + position + " " + isChecked)
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.quick_look_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemSetting -> openNotificationPermissionPage()
        }
        return super.onOptionsItemSelected(item)
    }
}
