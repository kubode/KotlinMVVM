package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.ext.createFavoriteObservable
import com.teamlab.kotlin.mvvm.ext.destroyFavoriteObservable
import com.teamlab.kotlin.mvvm.util.Toaster
import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.value
import twitter4j.Status

class Tweet(private val account: Account, status: Status) : Model<Pair<Account, Long>>() {
    override val id = Pair(account, status.id)
    val text = status.text
    val createdAt = status.createdAt
    val favoriteCountObservable = RxPropertyObservable.value(status.favoriteCount)
    private var favoriteCount by favoriteCountObservable.asProperty()
    val isFavoritedObservable = RxPropertyObservable.value(status.isFavorited)
    private var isFavorited by isFavoritedObservable.asProperty()
    val isFavoriteRequestingObservable = RxPropertyObservable.value(false)
    private var isFavoriteRequesting by isFavoriteRequestingObservable.asProperty()

    fun merge(status: Status) {
        favoriteCount = status.favoriteCount
        isFavorited = status.isFavorited
    }

    fun toggleFavoriteIfEnable() {
        if (isFavoriteRequesting) return
        isFavoriteRequesting = true
        val observable = if (isFavorited) {
            account.twitter.destroyFavoriteObservable(id.second)
        } else {
            account.twitter.createFavoriteObservable(id.second)
        }
        observable
                .finallyDo { isFavoriteRequesting = false }
                .subscribe({
                    merge(it)
                }, {
                    Toaster.show(account.context, it.message)
                })
    }
}
