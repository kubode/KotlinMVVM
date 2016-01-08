package com.teamlab.kotlin.mvvm.model

import android.content.Context
import com.teamlab.kotlin.mvvm.ext.getOAuthAccessTokenObservable
import com.teamlab.kotlin.mvvm.ext.getOAuthRequestTokenObservable
import rx.mvvm.RxPropertyObservable
import rx.mvvm.rxProperty
import rx.mvvm.value
import twitter4j.Twitter
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken

class OAuth(private val context: Context, private val twitter: Twitter) {

    val stateObservable = RxPropertyObservable.value<State>()
    private var state by rxProperty(State.NORMAL, stateObservable)
    val errorObservable = RxPropertyObservable.value<Throwable?>()
    private var error by rxProperty(null, errorObservable)
    val requestTokenObservable = RxPropertyObservable.value<RequestToken?>()
    private var requestToken by rxProperty(null, requestTokenObservable)
    val accessTokenObservable = RxPropertyObservable.value<AccessToken?>()
    private var accessToken by rxProperty(null, accessTokenObservable)

    fun getRequestTokenIfEnable() {
        if (state == State.REQUESTING) return
        state = State.REQUESTING
        error = null
        twitter.getOAuthRequestTokenObservable()
                .subscribe({
                    state = State.NORMAL
                    requestToken = it
                }, {
                    state = State.ERROR
                    error = it
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
                }, {
                    state = State.ERROR
                    error = it
                })
    }

    fun obtainAccount() = Account(context, twitter, accessToken!!)
}
