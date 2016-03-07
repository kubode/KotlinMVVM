package com.teamlab.kotlin.mvvm.ui.views

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding.view.clicks
import com.squareup.picasso.Picasso
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.data.model.Tweet
import com.teamlab.kotlin.mvvm.ui.viewmodels.TweetViewModel
import com.teamlab.kotlin.mvvm.util.bindView
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class TweetViewHolder(parent: ViewGroup, private val picasso: Picasso) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tweet, parent, false)) {
    private val userProfileImage by bindView<ImageView>(R.id.user_profile_image)
    private val userName by bindView<TextView>(R.id.user_name)
    private val createdAt by bindView<TextView>(R.id.created_at)
    private val text by bindView<TextView>(R.id.text)
    private val favorite by bindView<TextView>(R.id.favorite)
    private val retweet by bindView<TextView>(R.id.retweet)

    private lateinit var vm: TweetViewModel
    private var isVmInitialized = false
    private lateinit var subscription: Subscription

    fun performBind(tweet: Tweet) {
        if (isVmInitialized) {
            vm.tweet = tweet
        } else {
            vm = TweetViewModel(tweet)
        }
        createdAt.text = DateFormat.format("yyyy/MM/dd kk:mm:ss", tweet.createdAt)
        text.text = tweet.text
    }

    fun performAttach() {
        subscription = CompositeSubscription(
                userProfileImage.bind(vm.userProfileImageUrlObservable) { picasso.load(it).into(this) },
                userName.bind(vm.userNameObservable) { text = it },
                favorite.bind(vm.favoriteIconResObservable) { setCompoundDrawablesRelativeWithIntrinsicBounds(it, 0, 0, 0) },
                favorite.bind(vm.favoriteCountObservable) { text = it },
                favorite.clicks().subscribe { vm.tweet.toggleFavoriteIfEnable() },
                retweet.bind(vm.retweetIconResObservable) { setCompoundDrawablesRelativeWithIntrinsicBounds(it, 0, 0, 0) },
                retweet.bind(vm.retweetCountObservable) { text = it })
    }

    fun performDetach() {
        subscription.unsubscribe()
    }
}
