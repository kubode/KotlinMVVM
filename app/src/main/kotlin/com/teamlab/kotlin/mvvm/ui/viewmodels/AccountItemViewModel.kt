package com.teamlab.kotlin.mvvm.ui.viewmodels

import com.teamlab.kotlin.mvvm.data.model.Account
import rx.mvvm.ViewModel
import rx.property.RxPropertyObservable
import rx.property.chain

class AccountItemViewModel(private val account: Account) : ViewModel() {
    val id = account.id
    val screenNameObservable = RxPropertyObservable.chain(account.screenNameObservable)
}
