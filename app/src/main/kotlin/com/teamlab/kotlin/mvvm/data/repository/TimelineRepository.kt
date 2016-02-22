package com.teamlab.kotlin.mvvm.data.repository

import com.teamlab.kotlin.mvvm.data.model.Account
import com.teamlab.kotlin.mvvm.data.model.Timeline
import com.teamlab.kotlin.mvvm.di.AccountScope
import javax.inject.Inject

@AccountScope
class TimelineRepository @Inject constructor(private val account: Account,
                                             private val cache: ModelCache) {

    fun of(identifier: Timeline.Identifier): Timeline {
        return cache.getOrPut(Timeline::class, identifier, { Timeline(account, it) })
    }
}
