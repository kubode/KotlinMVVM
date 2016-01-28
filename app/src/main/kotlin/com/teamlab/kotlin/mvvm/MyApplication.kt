package com.teamlab.kotlin.mvvm

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.di.DaggerMyApplicationComponent
import com.teamlab.kotlin.mvvm.di.MyApplicationComponent
import com.teamlab.kotlin.mvvm.di.MyApplicationModule

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
