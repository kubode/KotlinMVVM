package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.RxProperty
import rx.Observable
import rx.schedulers.Schedulers
import twitter4j.Twitter
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken

class OAuth(private val twitter: Twitter,
            private val pref: MyPreferences) {

    private val consumerKey = "hZOuxdz51CludNkjEyMuiR6JB"
    private val consumerSecret = "2gtKvKETxmDlj0P9VNrzUQgm3JY6m8To1Trd5lqU8sOsPsyRMh"

    val status = RxProperty(Status.NORMAL)
    val error = RxProperty(null as Throwable?)
    val authorizationUrl = RxProperty("")

    private var requestToken: RequestToken? = null

    init {
        twitter.setOAuthConsumer(consumerKey, consumerSecret)
    }

    fun initialize() {
        if (isAccessTokenStored()) {
            twitter.oAuthAccessToken = obtainAccessToken()
            status.value = Status.COMPLETED
            return
        }
        getRequestToken()
    }

    fun getRequestToken() {
        status.value = Status.REQUESTING
        Observable.create<RequestToken> { it.onNext(twitter.oAuthRequestToken) }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    requestToken = it
                    authorizationUrl.value = it.authorizationURL
                    status.value = Status.NORMAL
                }, {
                    error.value = it
                    status.value = Status.ERROR
                })
    }

    fun auth(pin: String) {
        status.value = Status.REQUESTING
        Observable.create<AccessToken> { it.onNext(twitter.getOAuthAccessToken(requestToken, pin)) }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    saveAccessToken(it)
                    twitter.oAuthAccessToken = obtainAccessToken()
                    status.value = Status.COMPLETED
                }, {
                    error.value = it
                    status.value = Status.ERROR
                })
    }

    private fun isAccessTokenStored(): Boolean {
        return !pref.accessToken.value.isEmpty() && !pref.accessTokenSecret.value.isEmpty()
    }

    private fun obtainAccessToken(): AccessToken {
        return AccessToken(pref.accessToken.value, pref.accessTokenSecret.value)
    }

    private fun saveAccessToken(accessToken: AccessToken) {
        pref.accessToken.value = accessToken.token
        pref.accessTokenSecret.value = accessToken.tokenSecret
    }
}
