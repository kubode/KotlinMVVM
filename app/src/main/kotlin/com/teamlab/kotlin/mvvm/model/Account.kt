package com.teamlab.kotlin.mvvm.model

import android.content.Context
import android.content.SharedPreferences
import com.teamlab.kotlin.mvvm.di.AccountModule
import com.teamlab.kotlin.mvvm.di.ApplicationComponent
import com.teamlab.kotlin.mvvm.di.DaggerAccountComponent
import com.teamlab.kotlin.mvvm.ext.MyTwitterFactory
import com.teamlab.kotlin.mvvm.util.SharedPreferencesProperty
import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.strPref
import twitter4j.auth.AccessToken

class Account(val context: Context, private val twitterFactory: MyTwitterFactory, userId: Long) : Model<Long>() {
    val twitter = twitterFactory.create()
    override val id = userId
    private val pref = context.getSharedPreferences("account-$userId", Context.MODE_PRIVATE)
    val screenNameObservable = RxPropertyObservable.strPref(pref, "screenName", "")
    private var isInitialized by SharedPreferencesProperty(pref, "isInitialized", false, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)
    private var token by SharedPreferencesProperty(pref, "token", "", SharedPreferences::getString, SharedPreferences.Editor::putString)
    private var tokenSecret by SharedPreferencesProperty(pref, "tokenSecret", "", SharedPreferences::getString, SharedPreferences.Editor::putString)
    private var screenName by screenNameObservable.toProperty()

    val component = DaggerAccountComponent.builder()
            .applicationComponent(ApplicationComponent.from(context))
            .accountModule(AccountModule(this))
            .build()

    init {
        if (isInitialized) {
            setupAccessToken()
        }
    }

    private fun setupAccessToken() {
        twitter.oAuthAccessToken = AccessToken(token, tokenSecret)
    }

    fun initialize(token: AccessToken) {
        this.isInitialized = true
        this.token = token.token
        this.tokenSecret = token.tokenSecret
        this.screenName = token.screenName
        setupAccessToken()
    }

    fun delete() = pref.edit().clear().commit()
}
