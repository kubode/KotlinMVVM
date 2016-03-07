package com.teamlab.kotlin.mvvm.ui.viewmodels

import com.teamlab.kotlin.mvvm.data.model.Tweet
import rx.mvvm.ViewModel
import rx.property.RxPropertyObservable
import rx.property.value

class TweetViewModel(tweet: Tweet) : ViewModel() {
    private val tweetObservable = RxPropertyObservable.value(tweet)
    var tweet by tweetObservable.toProperty()

    val userProfileImageUrlObservable = tweetObservable.flatMap { it.user.profileImageUrlObservable }
    val userNameObservable = tweetObservable.flatMap { it.user.nameObservable }
    val favoriteIconResObservable = tweetObservable.flatMap { it.isFavoritedObservable }.map { if (it) android.R.drawable.star_on else android.R.drawable.star_off }
    val favoriteCountObservable = tweetObservable.flatMap { it.favoriteCountObservable }.map { String.format("%1$,3d", it) }
    val retweetIconResObservable = tweetObservable.flatMap { it.isRetweetedObservable }.map { if (it) android.R.drawable.checkbox_on_background else android.R.drawable.checkbox_off_background }
    val retweetCountObservable = tweetObservable.flatMap { it.retweetCountObservable }.map { String.format("%1$,3d", it) }
}
