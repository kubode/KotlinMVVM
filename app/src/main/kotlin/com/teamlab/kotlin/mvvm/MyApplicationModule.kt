package com.teamlab.kotlin.mvvm

import android.content.Context
import com.teamlab.kotlin.mvvm.model.MyPreferences
import com.teamlab.kotlin.mvvm.model.Twitter
import com.teamlab.kotlin.mvvm.util.Module

class MyApplicationModule(application: MyApplication) : Module() {
    init {
        provideSingleton(MyApplication::class, { application })
        provideSingleton(Context::class, { application })
        provideSingleton(MyPreferences::class, { MyPreferences(it.get(Context::class)) })
        provideSingleton(Twitter::class, { Twitter(it.get(MyPreferences::class)) })
    }
}
