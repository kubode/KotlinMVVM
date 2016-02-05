package com.teamlab.kotlin.mvvm.ui.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.kubode.rxeventbus.RxEventBus
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.data.model.Account
import com.teamlab.kotlin.mvvm.event.AccountClickEvent
import com.teamlab.kotlin.mvvm.util.bindView

class AccountViewHolder(parent: ViewGroup, private val bus: RxEventBus) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.account, parent, false)) {
    private val thumbnail by bindView<ImageView>(R.id.thumbnail)
    private val userId by bindView<TextView>(R.id.user_id)
    private val screenName by bindView<TextView>(R.id.screen_name)
    fun performBind(account: Account) {
        itemView.setOnClickListener { bus.post(AccountClickEvent(account)) }
        userId.text = "${account.id}"
        screenName.text = "${account.screenNameObservable}"
    }
}
