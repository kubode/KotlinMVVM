package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.github.kubode.rxeventbus.RxEventBus
import com.jakewharton.rxbinding.support.v4.widget.refreshes
import com.jakewharton.rxbinding.view.clicks
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.di.AccountComponent
import com.teamlab.kotlin.mvvm.event.TweetEvent
import com.teamlab.kotlin.mvvm.util.bindView
import com.teamlab.kotlin.mvvm.viewmodel.TimelineViewModel
import rx.Subscription
import rx.mvvm.bind
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class TimelineFragment : Fragment() {

    @Inject lateinit var vm: TimelineViewModel
    @Inject lateinit var bus: RxEventBus

    private val swipe by bindView<SwipeRefreshLayout>(R.id.swipe)
    private val recycler by bindView<RecyclerView>(R.id.recycler)
    private val progress by bindView<ProgressBar>(R.id.progress)
    private val error by bindView<View>(R.id.error)
    private val message by bindView<TextView>(R.id.message)
    private val retry by bindView<View>(R.id.retry)
    private val add by bindView<View>(R.id.add)

    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        AccountComponent.from(activity).inject(this)

        super.onCreate(savedInstanceState)
        vm.getInitTweetsIfEnable()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.timeline, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = TimelineAdapter(vm.tweets)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(activity)
        subscription = CompositeSubscription(
                bind(vm.tweetsAddObservable) { adapter.notifyItemRangeInserted(it.start, it.count) },
                swipe.refreshes().subscribe { vm.getNewTweetsIfEnable() },
                swipe.bind(vm.isRefreshingObservable) { isRefreshing = it },
                progress.bind(vm.isInitProgressVisibleObservable) { visibility = if (it) View.VISIBLE else View.GONE },
                error.bind(vm.isInitErrorVisibleObservable) { visibility = if (it) View.VISIBLE else View.GONE },
                message.bind(vm.initErrorMessageObservable) { text = it },
                retry.clicks().subscribe { vm.getInitTweetsIfEnable() },
                add.clicks().subscribe { bus.post(TweetEvent()) }
        )
    }

    override fun onDestroyView() {
        subscription.unsubscribe()
        super.onDestroyView()
    }
}
