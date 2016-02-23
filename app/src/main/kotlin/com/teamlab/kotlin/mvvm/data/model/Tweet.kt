package com.teamlab.kotlin.mvvm.data.model

import com.teamlab.kotlin.mvvm.data.repository.UserRepository
import com.teamlab.kotlin.mvvm.extensions.showToast
import com.teamlab.kotlin.mvvm.util.createFavoriteObservable
import com.teamlab.kotlin.mvvm.util.destroyFavoriteObservable
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

    init {
        account.component.inject(this)
        user = userRepository.of(status.user)
        text = status.text
        createdAt = status.createdAt
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
                    account.context.showToast(it.message)
                })
    }
}
