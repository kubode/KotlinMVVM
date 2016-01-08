package com.teamlab.kotlin.mvvm

import android.content.Context
import com.teamlab.kotlin.mvvm.model.AppPreferences
import com.teamlab.kotlin.mvvm.util.EventBus
import com.teamlab.kotlin.mvvm.util.Module

class MyApplicationModule(application: MyApplication) : Module() {
    init {
        provideSingleton(MyApplication::class, { application })
        provideSingleton(Context::class, { application })
        provideSingleton(AppPreferences::class, { AppPreferences(get(Context::class)) })
        provideSingleton(EventBus::class, { EventBus() })
    }
}
