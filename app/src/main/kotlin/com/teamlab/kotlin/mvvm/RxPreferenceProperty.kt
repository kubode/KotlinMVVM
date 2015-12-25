package com.teamlab.kotlin.mvvm

import android.content.SharedPreferences

class RxPreferenceProperty<T>(private val sharedPreferences: SharedPreferences,
                              private val get: SharedPreferences.(String, T) -> T,
                              private val set: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
                              private val key: String,
                              private val defaultValue: T) : RxProperty<T>(sharedPreferences.get(key, defaultValue)) {
    init {
        super.value = this.value
    }

    override var value: T
        get() = sharedPreferences.get(key, defaultValue)
        set(value) {
            sharedPreferences.edit().set(key, value).apply()
            super.value = value
        }
}
