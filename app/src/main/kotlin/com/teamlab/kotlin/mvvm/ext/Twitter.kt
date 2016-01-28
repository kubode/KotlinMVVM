package com.teamlab.kotlin.mvvm.ext

import rx.Observable
import rx.schedulers.Schedulers
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.RequestToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyTwitterFactory @Inject constructor() {
    private val CONSUMER_KEY = "hZOuxdz51CludNkjEyMuiR6JB"
    private val CONSUMER_SECRET = "2gtKvKETxmDlj0P9VNrzUQgm3JY6m8To1Trd5lqU8sOsPsyRMh"

    fun create(): Twitter {
        return TwitterFactory().instance.apply {
            setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET)
        }
    }
}

private fun <T> asObservable(f: () -> T): Observable<T> {
    return Observable
            .create<T> {
                try {
                    val r = f()
                    if (it.isUnsubscribed)
                        return@create
                    it.onNext(r)
                    it.onCompleted()
                } catch(e: Throwable) {
                    if (it.isUnsubscribed)
                        return@create
                    it.onError(e)
                }
            }
            .subscribeOn(Schedulers.io())
}

fun Twitter.getOAuthRequestTokenObservable() = asObservable { oAuthRequestToken }
fun Twitter.getOAuthAccessTokenObservable(requestToken: RequestToken, pin: String) = asObservable { getOAuthAccessToken(requestToken, pin) }
fun Twitter.createFavoriteObservable(id: Long) = asObservable { createFavorite(id) }
fun Twitter.destroyFavoriteObservable(id: Long) = asObservable { destroyFavorite(id) }
