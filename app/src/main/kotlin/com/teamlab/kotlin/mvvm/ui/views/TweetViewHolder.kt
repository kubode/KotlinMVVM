package com.teamlab.kotlin.mvvm.ui.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.data.model.Tweet
import com.teamlab.kotlin.mvvm.util.bindView

class TweetViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tweet, parent, false)) {
    private val text by bindView<TextView>(R.id.text)
    fun performBind(tweet: Tweet) {
        text.text = tweet.text
    }
}
