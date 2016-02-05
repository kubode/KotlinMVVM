package com.teamlab.kotlin.mvvm

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.di.ApplicationComponent
import com.teamlab.kotlin.mvvm.di.ApplicationModule
import com.teamlab.kotlin.mvvm.di.DaggerApplicationComponent

class MyApplication : Application() {

    lateinit var ref: RefWatcher
    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        ref = LeakCanary.install(this)
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}
