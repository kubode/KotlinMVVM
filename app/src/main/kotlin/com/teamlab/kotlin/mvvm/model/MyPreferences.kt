package com.teamlab.kotlin.mvvm.model

import android.content.Context
import android.content.SharedPreferences
import com.teamlab.kotlin.mvvm.RxPreferenceProperty

class MyPreferences(context: Context) {
    private val pref = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    val accessToken = RxPreferenceProperty<String>(pref, SharedPreferences::getString, SharedPreferences.Editor::putString, "accessToken", "")
    val accessTokenSecret = RxPreferenceProperty<String>(pref, SharedPreferences::getString, SharedPreferences.Editor::putString, "accessTokenSecret", "")
}
