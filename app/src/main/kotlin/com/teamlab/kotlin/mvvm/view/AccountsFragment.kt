package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.kubode.rxeventbus.RxEventBus
import com.jakewharton.rxbinding.view.clicks
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.MyApplicationComponent
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.event.AddAccountEvent
import com.teamlab.kotlin.mvvm.model.Account
import com.teamlab.kotlin.mvvm.util.bindView
import com.teamlab.kotlin.mvvm.util.logV
import com.teamlab.kotlin.mvvm.viewmodel.AccountsViewModel
import rx.Subscription
import rx.mvvm.bind
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject
import kotlin.properties.Delegates

class AccountsFragment : Fragment() {

    @Inject lateinit var ref: RefWatcher
    @Inject lateinit var bus: RxEventBus
    @Inject lateinit var vm: AccountsViewModel

    private val recycler by bindView<RecyclerView>(R.id.recycler)
    private val add by bindView<View>(R.id.add)

    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplicationComponent.from(this).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.accounts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(activity)
        subscription = CompositeSubscription(
                bind(vm.accountsObservable) { adapter.accounts = it },
                add.clicks().subscribe { bus.post(AddAccountEvent()) }
        )
    }

    override fun onDestroyView() {
        recycler.adapter = null
        subscription.unsubscribe()
        super.onDestroyView()
    }

    override fun onDestroy() {
        vm.performDestroy()
        super.onDestroy()
        ref.watch(this)
    }

    private val adapter = object : RecyclerView.Adapter<ViewHolder>() {
        var accounts: List<Account> by Delegates.observable(emptyList(), { property, old, new -> logV({ "$old -> $new" }).run { notifyDataSetChanged() } })
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = logV({ "onCreateViewHolder" }).run { ViewHolder(parent) }
        override fun getItemCount() = accounts.size.apply { logV({ "getItemCount() = $this" }) }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = logV({ "onBindViewHolder" }).run { holder.performBind(accounts[position]) }
        override fun onViewAttachedToWindow(holder: ViewHolder) = logV({ "onViewAttachedToWindow" })
        override fun onViewDetachedFromWindow(holder: ViewHolder) = logV({ "onViewDetachedFromWindow" })
    }

    private class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.account, parent, false)) {
        private val thumbnail by bindView<ImageView>(R.id.thumbnail)
        private val userId by bindView<TextView>(R.id.user_id)
        private val screenName by bindView<TextView>(R.id.screen_name)
        fun performBind(account: Account) {
            userId.text = "${account.id}"
            screenName.text = "${account.screenNameObservable}"
        }
    }
}
