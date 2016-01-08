package rx.mvvm

import android.content.SharedPreferences
import rx.Observable
import rx.subscriptions.Subscriptions

fun <T> RxPropertyObservable.Companion.pref(sharedPreferences: SharedPreferences,
                                            key: String,
                                            defValue: T,
                                            get: SharedPreferences.(String, T) -> T,
                                            put: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor)
        = RxPropertyObservable(PrefState(sharedPreferences, key, defValue, get, put))

fun RxPropertyObservable.Companion.strPref(sharedPreferences: SharedPreferences,
                                           key: String,
                                           defValue: String)
        = RxPropertyObservable(PrefState(sharedPreferences, key, defValue, SharedPreferences::getString, SharedPreferences.Editor::putString))

private class PrefState<T>(private val sharedPreferences: SharedPreferences,
                           private val key: String,
                           private val defValue: T,
                           private val get: SharedPreferences.(String, T) -> T,
                           private val put: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor) : State<T>() {
    override val observable = Observable.create<T> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == this.key) {
                it.onNext(value)
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        it.add(Subscriptions.create { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) })
        it.onNext(value)
    }
    override var value: T
        get() = sharedPreferences.get(key, defValue)
        set(value) {
            sharedPreferences.edit().put(key, value).apply()
        }
}
