package com.noest.notidirect

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.noest.notidirect.receiver.UnlockReceiver
import com.noest.notidirect.utils.LogX
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchScreenOnJump.setOnCheckedChangeListener { buttonView, isChecked ->
            LogX.d(TAG, "checked: " + isChecked)
            if (isChecked) {
                UnlockReceiver.regReceiver(this)
            } else {
                UnlockReceiver.unRegReceiver(this)
            }
        }
    }
}
