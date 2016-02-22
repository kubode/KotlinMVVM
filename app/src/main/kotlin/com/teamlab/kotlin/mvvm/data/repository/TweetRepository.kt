package com.teamlab.kotlin.mvvm.data.repository

import com.teamlab.kotlin.mvvm.data.model.Account
import com.teamlab.kotlin.mvvm.data.model.Tweet
import com.teamlab.kotlin.mvvm.di.AccountScope
import twitter4j.Status
import javax.inject.Inject

@AccountScope
class TweetRepository @Inject constructor(private val account: Account,
                                          private val cache: ModelCache) {

    fun of(status: Status): Tweet {
        return cache.getOrPut(Tweet::class, status.id, { Tweet(account, status) })
                .apply { merge(status) }
    }
}
