package com.teamlab.kotlin.mvvm.util

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferencesProperty<T>(private val sharedPreferences: SharedPreferences,
                                   private val key: String,
                                   private val defaultValue: T,
                                   private val get: SharedPreferences.(String, T) -> T,
                                   private val put: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor) : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return sharedPreferences.get(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        sharedPreferences.edit().put(key, value).apply()
    }
}
