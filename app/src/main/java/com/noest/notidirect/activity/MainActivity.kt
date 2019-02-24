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
                .setTitle("使用说明")
                .setMessage(
                    "1、快读正常功能使用需要通知使用权，需点击右上角设置按钮进入通知使用权管理页，并授予快读通知使用权" +
                            "\n2、为避免快读进程被清理影响功能使用，需在当前页面调出最近任务列表并锁定快读该应用" +
                            "\n3、不在列表中的应用在显示通知后会加入到列表中" +
                            "\n4、设备重启后，需同1相同步骤进入通知使用权管理页，关闭然后开启快读的通知使用权"
                )
                .setPositiveButton("确定") { _, _ ->
                }
                .setNegativeButton("不再提示") { _, _ ->
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
