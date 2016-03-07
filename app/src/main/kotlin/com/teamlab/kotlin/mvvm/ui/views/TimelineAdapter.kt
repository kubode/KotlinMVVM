package com.teamlab.kotlin.mvvm.ui.views

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.teamlab.kotlin.mvvm.data.model.Tweet
import com.teamlab.kotlin.mvvm.util.logV

class TimelineAdapter(private val tweets: List<Tweet>, private val picasso: Picasso) : RecyclerView.Adapter<TweetViewHolder>() {
    override fun getItemCount() = tweets.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        logV({ "onCreateViewHolder($parent, $viewType)" })
        return TweetViewHolder(parent, picasso)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        logV({ "onBindViewHolder($holder, $position)" })
        holder.performBind(tweets[position])
    }

    override fun onViewAttachedToWindow(holder: TweetViewHolder) {
        logV({ "onViewAttachedToWindow($holder)" })
        holder.performAttach()
    }

    override fun onViewDetachedFromWindow(holder: TweetViewHolder) {
        logV({ "onViewDetachedFromWindow($holder)" })
        holder.performDetach()
    }
}
