package com.teamlab.kotlin.mvvm.ui.view

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.teamlab.kotlin.mvvm.data.model.Tweet

class TimelineAdapter(private val tweets: List<Tweet>) : RecyclerView.Adapter<TweetViewHolder>() {
    override fun getItemCount() = tweets.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        return TweetViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        holder.performBind(tweets[position])
    }
}
