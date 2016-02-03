package com.teamlab.kotlin.mvvm.repository

import com.teamlab.kotlin.mvvm.di.AccountScope
import com.teamlab.kotlin.mvvm.model.Account
import com.teamlab.kotlin.mvvm.model.Timeline
import rx.mvvm.Cache
import javax.inject.Inject

@AccountScope
class TimelineRepository @Inject constructor(private val account: Account) {

    private val cache = Cache<Timeline, Timeline.Identifier>()

    fun of(identifier: Timeline.Identifier): Timeline {
        return cache.getOrPut(identifier, { Timeline(account, identifier) })
    }
}
