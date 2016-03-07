package com.teamlab.kotlin.mvvm.data.model

import com.teamlab.kotlin.mvvm.data.repository.UserRepository
import com.teamlab.kotlin.mvvm.util.createFavoriteObservable
import com.teamlab.kotlin.mvvm.util.destroyFavoriteObservable
import com.teamlab.kotlin.mvvm.util.showToast
import rx.mvvm.Model
import rx.property.RxPropertyObservable
import rx.property.value
import twitter4j.Status
import twitter4j.Twitter
import java.util.*
import javax.inject.Inject

class Tweet(private val account: Account, status: Status) : Model<Long>() {
    override val id = status.id

    @Inject lateinit var twitter: Twitter
    @Inject lateinit var userRepository: UserRepository

    // Immutable properties
    val user: User
    val text: String
    val createdAt: Date

    // Mutable properties
    val favoriteCountObservable = RxPropertyObservable.value(0)
    private var favoriteCount by favoriteCountObservable.toProperty()
    val isFavoritedObservable = RxPropertyObservable.value(false)
    private var isFavorited by isFavoritedObservable.toProperty()
    val isFavoriteRequestingObservable = RxPropertyObservable.value(false)
    private var isFavoriteRequesting by isFavoriteRequestingObservable.toProperty()

    val retweetCountObservable = RxPropertyObservable.value(0)
    private var retweetCount by retweetCountObservable.toProperty()
    val isRetweetedObservable = RxPropertyObservable.value(false)
    private var isRetweeted by isRetweetedObservable.toProperty()
    val isRetweetRequestingObservable = RxPropertyObservable.value(false)
    private var isRetweetRequesting by isRetweetRequestingObservable.toProperty()

    init {
        account.component.inject(this)
        user = userRepository.of(status.user)
        text = status.text
        createdAt = status.createdAt
    }

    fun merge(status: Status) {
        favoriteCount = status.favoriteCount
        isFavorited = status.isFavorited
        retweetCount = status.retweetCount
        isRetweeted = status.isRetweeted
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
                .doAfterTerminate { isFavoriteRequesting = false }
                .subscribe({
                    merge(it)
                }, {
                    account.context.showToast(it.message)
                })
    }
}
