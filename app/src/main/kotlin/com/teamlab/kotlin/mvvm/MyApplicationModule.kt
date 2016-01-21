package com.teamlab.kotlin.mvvm

import android.content.Context
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.model.AppPreferences
import com.teamlab.kotlin.mvvm.util.RxEventBus
import com.teamlab.kotlin.mvvm.util.Module

class MyApplicationModule(application: MyApplication) : Module() {
    init {
        provideSingleton(MyApplication::class, { application })
        provideSingleton(Context::class, { application })
        provideSingleton(RefWatcher::class, { application.ref })
        provideSingleton(AppPreferences::class, { AppPreferences(get(Context::class)) })
        provideSingleton(RxEventBus::class, { RxEventBus() })
    }
}
