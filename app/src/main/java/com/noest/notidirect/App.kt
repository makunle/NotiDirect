package com.noest.notidirect

import android.app.Application
import android.content.Context
import com.noest.notidirect.utils.minicache.MiniCache

class App : Application() {
    companion object {
        var app: Application? = null
        fun getContext(): Context {
            return app!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        MiniCache.init(cacheDir.absolutePath)
    }


}