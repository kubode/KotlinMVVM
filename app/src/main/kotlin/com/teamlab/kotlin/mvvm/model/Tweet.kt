package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.ext.createFavoriteObservable
import com.teamlab.kotlin.mvvm.ext.destroyFavoriteObservable
import com.teamlab.kotlin.mvvm.util.Toaster
import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.value
import twitter4j.Status
import twitter4j.Twitter
import javax.inject.Inject

class Tweet(private val account: Account, status: Status) : Model<Long>() {
    override val id = status.id

    @Inject lateinit var twitter: Twitter

    val text = status.text
    val createdAt = status.createdAt
    val favoriteCountObservable = RxPropertyObservable.value(status.favoriteCount)
    private var favoriteCount by favoriteCountObservable.toProperty()
    val isFavoritedObservable = RxPropertyObservable.value(status.isFavorited)
    private var isFavorited by isFavoritedObservable.toProperty()
    val isFavoriteRequestingObservable = RxPropertyObservable.value(false)
    private var isFavoriteRequesting by isFavoriteRequestingObservable.toProperty()

    init {
        account.component.inject(this)
    }

    fun merge(status: Status) {
        favoriteCount = status.favoriteCount
        isFavorited = status.isFavorited
    }

    fun toggleFavoriteIfEnable() {
        if (isFavoriteRequesting) return
        isFavoriteRequesting = true
        val observable = if (isFavorited) {
            twitter.destroyFavoriteObservable(id)
        } else {
            twitter.createFavoriteObservable(id)
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
