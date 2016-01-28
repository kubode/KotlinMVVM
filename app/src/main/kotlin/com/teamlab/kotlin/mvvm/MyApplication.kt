package com.teamlab.kotlin.mvvm

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class MyApplication : Application() {

    lateinit var ref: RefWatcher
    lateinit var component: MyApplicationComponent

    override fun onCreate() {
        super.onCreate()
        ref = LeakCanary.install(this)
        component = DaggerMyApplicationComponent.builder()
                .myApplicationModule(MyApplicationModule(this))
                .build()
    }
}
