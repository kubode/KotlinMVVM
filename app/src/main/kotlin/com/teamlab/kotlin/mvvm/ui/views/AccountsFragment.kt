package com.teamlab.kotlin.mvvm.ui.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.kubode.rxeventbus.RxEventBus
import com.jakewharton.rxbinding.view.clicks
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.di.ApplicationComponent
import com.teamlab.kotlin.mvvm.event.AddAccountEvent
import com.teamlab.kotlin.mvvm.ui.viewmodels.AccountsViewModel
import com.teamlab.kotlin.mvvm.util.bindView
import com.teamlab.kotlin.mvvm.util.unbindViews
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class AccountsFragment : Fragment() {

    @Inject lateinit var ref: RefWatcher
    @Inject lateinit var bus: RxEventBus
    @Inject lateinit var vm: AccountsViewModel
    @Inject lateinit var adapter: AccountsAdapter

    private val recycler by bindView<RecyclerView>(R.id.recycler)
    private val add by bindView<View>(R.id.add)

    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        ApplicationComponent.from(this).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.accounts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(activity)
        subscription = CompositeSubscription(
                add.clicks().subscribe { bus.post(AddAccountEvent()) }
        )
    }

    override fun onDestroyView() {
        recycler.adapter = null
        subscription.unsubscribe()
        unbindViews()
        super.onDestroyView()
    }

    override fun onDestroy() {
        vm.performDestroy()
        adapter.performDestroy()
        super.onDestroy()
        ref.watch(this)
    }
}
