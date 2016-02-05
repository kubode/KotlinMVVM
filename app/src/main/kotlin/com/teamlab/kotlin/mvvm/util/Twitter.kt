package com.teamlab.kotlin.mvvm.util

import rx.Observable
import rx.schedulers.Schedulers
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.auth.RequestToken

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
fun Twitter.getHomeTimelineObservable(paging: Paging) = asObservable { getHomeTimeline(paging) }
