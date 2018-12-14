package com.noest.notidirect

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        var app: Application? = null
        fun getContext(): Context {
            return app!!.applicationContext
        }
    }

    init {
        app = this
    }


}