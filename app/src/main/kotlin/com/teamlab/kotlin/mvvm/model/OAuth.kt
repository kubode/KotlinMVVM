package com.teamlab.kotlin.mvvm.model

import android.content.Context
import com.teamlab.kotlin.mvvm.ext.getOAuthAccessTokenObservable
import com.teamlab.kotlin.mvvm.ext.getOAuthRequestTokenObservable
import com.teamlab.kotlin.mvvm.ext.of
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.InjectionHierarchy
import com.teamlab.kotlin.mvvm.util.inject
import com.teamlab.kotlin.mvvm.util.logV
import rx.mvvm.RxPropertyObservable
import rx.mvvm.rxProperty
import rx.mvvm.value
import twitter4j.Twitter
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken

class OAuth(private val context: Context, private val twitter: Twitter) : Injectable {
    override val injectionHierarchy = InjectionHierarchy.of(context)

    private val pref by inject(AppPreferences::class)

    val stateObservable = RxPropertyObservable.value<State>()
    private var state by rxProperty(State.NORMAL, stateObservable)
    val errorObservable = RxPropertyObservable.value<Throwable?>()
    private var error by rxProperty(null, errorObservable)
    val requestTokenObservable = RxPropertyObservable.value<RequestToken?>()
    private var requestToken by rxProperty(null, requestTokenObservable)
    val accessTokenObservable = RxPropertyObservable.value<AccessToken?>()
    private var accessToken by rxProperty(null, accessTokenObservable)

    fun getRequestTokenIfEnable() {
        logV({ "$state, $error, $requestToken, $accessToken" })
        if (state == State.REQUESTING) return
        state = State.REQUESTING
        error = null
        twitter.getOAuthRequestTokenObservable()
                .subscribe({
                    state = State.NORMAL
                    requestToken = it
                    logV({ "$state, $error, $requestToken, $accessToken" })
                }, {
                    state = State.ERROR
                    error = it
                    logV({ "$state, $error, $requestToken, $accessToken" })
                })
    }

    fun getAccessTokenIfEnable(pin: String) {
        if (state == State.REQUESTING) return
        state = State.REQUESTING
        error = null
        twitter.getOAuthAccessTokenObservable(requestToken!!, pin)
                .subscribe({
                    state = State.COMPLETED
                    accessToken = it
                    pref.accounts += obtainAccount()
                }, {
                    state = State.ERROR
                    error = it
                })
    }

    fun obtainAccount() = Account(context, twitter, accessToken!!)
}
