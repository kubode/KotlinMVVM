package com.teamlab.kotlin.mvvm.ui.viewmodel

import com.teamlab.kotlin.mvvm.data.model.State
import com.teamlab.kotlin.mvvm.data.model.Timeline
import com.teamlab.kotlin.mvvm.data.repository.TimelineRepository
import javax.inject.Inject

class TimelineViewModel @Inject constructor(private val timelineRepository: TimelineRepository) {

    private val timeline = timelineRepository.of(Timeline.Identifier.home())

    val tweets = timeline.tweets
    val tweetsAddObservable = timeline.tweetsAddObservable
    val isInitProgressVisibleObservable = timeline.initStateObservable.map { it == State.REQUESTING }
    val isInitErrorVisibleObservable = timeline.initStateObservable.map { it == State.ERROR }
    val initErrorMessageObservable = timeline.initErrorObservable.map { it?.message }
    val isRefreshingObservable = timeline.newStateObservable.map {it== State.REQUESTING}

    fun getInitTweetsIfEnable() = timeline.getInitTweetsIfEnable()
    fun getNewTweetsIfEnable() = timeline.getNewTweetsIfEnable()
    fun getMoreTweetsIfEnable() = timeline.getMoreTweetsIfEnable()
}
