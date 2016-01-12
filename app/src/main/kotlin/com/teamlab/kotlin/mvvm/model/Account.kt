package com.teamlab.kotlin.mvvm.model

import android.content.Context
import com.teamlab.kotlin.mvvm.ext.TwitterFactory
import rx.mvvm.*
import twitter4j.Twitter
import twitter4j.auth.AccessToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Account private constructor(val context: Context, val twitter: Twitter, userId: Long) : Model<Long>() {
    override val id = userId
    private val pref = context.getSharedPreferences("account-$userId", Context.MODE_PRIVATE)
    val screenNameObservable = RxPropertyObservable.strPref(pref, "screenName", "")
    private var token by StrPref("token")
    private var tokenSecret by StrPref("tokenSecret")
    private var screenName by rxProperty("", screenNameObservable)

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

    companion object {
        private val cache = Cache<Account, Long>()
        fun of(context: Context, userId: Long): Account {
            return cache.getAndPut(userId, { Account(context, TwitterFactory.create(), userId) })
        }
    }
}
