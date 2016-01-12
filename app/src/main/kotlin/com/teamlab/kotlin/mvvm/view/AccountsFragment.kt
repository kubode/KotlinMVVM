package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.view.clicks
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.event.AddAccountEvent
import com.teamlab.kotlin.mvvm.ext.of
import com.teamlab.kotlin.mvvm.util.*
import com.teamlab.kotlin.mvvm.viewmodel.AccountsViewModel
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class AccountsFragment : Fragment(), Injectable {
    override val injectionHierarchy = InjectionHierarchy.of(this)

    private val ref by inject(RefWatcher::class)
    private val bus by inject(EventBus::class)

    private val recycler by bindView<RecyclerView>(R.id.recycler)
    private val add by bindView<View>(R.id.add)

    private lateinit var vm: AccountsViewModel
    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = AccountsViewModel(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.accounts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscription = CompositeSubscription(
                add.clicks().subscribe { bus.post(AddAccountEvent()) }
        )
    }

    override fun onDestroyView() {
        subscription.unsubscribe()
        super.onDestroyView()
    }

    override fun onDestroy() {
        vm.performDestroy()
        super.onDestroy()
        ref.watch(this)
    }

    private class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
            throw UnsupportedOperationException()
        }

        override fun getItemCount(): Int {
            throw UnsupportedOperationException()
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            throw UnsupportedOperationException()
        }
    }
}
