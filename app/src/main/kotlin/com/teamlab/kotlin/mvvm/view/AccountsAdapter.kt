package com.teamlab.kotlin.mvvm.view

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.kubode.rxeventbus.RxEventBus
import com.teamlab.kotlin.mvvm.model.Account
import com.teamlab.kotlin.mvvm.model.AppPreferences
import javax.inject.Inject
import kotlin.properties.Delegates

class AccountsAdapter @Inject constructor(private val pref: AppPreferences, private val bus: RxEventBus) : RecyclerView.Adapter<AccountViewHolder>() {
    private var accounts: List<Account> by Delegates.observable(emptyList(), { property, old, new ->
        notifyDataSetChanged()
    })
    private val subscription = pref.accountsObservable.subscribe { accounts = it }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AccountViewHolder(parent, bus)
    override fun getItemCount() = accounts.size
    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) = holder.performBind(accounts[position])

    fun performDestroy() = subscription.unsubscribe()
}
