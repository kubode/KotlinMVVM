package com.teamlab.kotlin.mvvm.model

import android.content.Context
import com.teamlab.kotlin.mvvm.di.DaggerAccountComponent
import com.teamlab.kotlin.mvvm.di.ApplicationComponent
import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.strPref
import twitter4j.Twitter
import twitter4j.auth.AccessToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Account(val context: Context, val twitter: Twitter, userId: Long) : Model<Long>() {
    override val id = userId
    private val pref = context.getSharedPreferences("account-$userId", Context.MODE_PRIVATE)
    val screenNameObservable = RxPropertyObservable.strPref(pref, "screenName", "")
    private var token by StrPref("token")
    private var tokenSecret by StrPref("tokenSecret")
    private var screenName by screenNameObservable.toProperty()

    val component = DaggerAccountComponent.builder()
            .applicationComponent(ApplicationComponent.from(context))
            .build()

    fun initialize(token: AccessToken) {
        this.token = token.token
        this.tokenSecret = token.tokenSecret
        this.screenName = token.screenName
    }

    fun delete() = pref.edit().clear().commit()

    private inner class StrPref(private val key: String) : ReadWriteProperty<Any, String> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            return pref.getString(key, null)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            pref.edit().putString(key, value).apply()
        }
    }
}
