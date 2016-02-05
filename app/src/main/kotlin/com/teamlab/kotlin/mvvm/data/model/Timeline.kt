package com.teamlab.kotlin.mvvm.data.model

import com.teamlab.kotlin.mvvm.data.repository.TweetRepository
import com.teamlab.kotlin.mvvm.util.getHomeTimelineObservable
import rx.mvvm.Model
import rx.mvvm.ObservableArrayList
import rx.mvvm.RxPropertyObservable
import rx.mvvm.value
import twitter4j.Paging
import twitter4j.Twitter
import java.util.*
import javax.inject.Inject

class Timeline(private val account: Account, override val id: Timeline.Identifier) : Model<Timeline.Identifier>() {

    data class Identifier private constructor(val type: Timeline.Identifier.Type) {
        enum class Type {
            HOME
        }

        companion object {
            fun home() = Timeline.Identifier(Type.HOME)
        }
    }

    @Inject lateinit var twitter: Twitter
    @Inject lateinit var tweetRepository: TweetRepository

    init {
        account.component.inject(this)
    }

    val initStateObservable = RxPropertyObservable.value(State.NORMAL)
    private var initState by initStateObservable.toProperty()
    val initErrorObservable = RxPropertyObservable.value(null as Throwable?)
    private var initError by initErrorObservable.toProperty()
    val newStateObservable = RxPropertyObservable.value(State.NORMAL)
    private var newState by newStateObservable.toProperty()
    val newErrorObservable = RxPropertyObservable.value(null as Throwable?)
    private var newError by newErrorObservable.toProperty()
    val moreStateObservable = RxPropertyObservable.value(State.NORMAL)
    private var moreState by moreStateObservable.toProperty()
    val moreErrorObservable = RxPropertyObservable.value(null as Throwable?)
    private var moreError by moreErrorObservable.toProperty()

    private val tweetsInternal = ObservableArrayList<Tweet>()
    val tweets: List<Tweet> = Collections.unmodifiableList(tweetsInternal)
    val tweetsAddObservable = tweetsInternal.addObservable

    fun getInitTweetsIfEnable() {
        if (initState in arrayOf(State.REQUESTING, State.COMPLETED)) return
        initState = State.REQUESTING
        initError = null
        twitter.getHomeTimelineObservable(Paging())
                .subscribe({
                    tweetsInternal += it.map { tweetRepository.of(it) }
                    initState = State.COMPLETED
                }, {
                    initError = it
                    initState = State.ERROR
                })
    }

    fun getNewTweetsIfEnable() {
        if (newState == State.REQUESTING) return
        newState = State.REQUESTING
        newError = null
        val paging = tweetsInternal.firstOrNull()?.let {
            Paging(it.id)
        } ?: run {
            Paging()
        }
        twitter.getHomeTimelineObservable(paging)
                .subscribe({
                    tweetsInternal.addAll(0, it.map { tweetRepository.of(it) })
                    newState = State.COMPLETED
                }, {
                    newError = it
                    newState = State.ERROR
                })
    }

    fun getMoreTweetsIfEnable() {
        if (moreState == State.REQUESTING) return
        moreState = State.REQUESTING
        moreError = null
        val paging = tweetsInternal.lastOrNull()?.let {
            Paging().apply { maxId = it.id }
        } ?: run {
            Paging()
        }
        twitter.getHomeTimelineObservable(paging)
                .subscribe({
                    tweetsInternal += it.map { tweetRepository.of(it) }
                    moreState = State.COMPLETED
                }, {
                    moreError = it
                    moreState = State.ERROR
                })
    }
}
