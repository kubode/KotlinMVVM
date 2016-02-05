package com.teamlab.kotlin.mvvm.ui.viewmodel

import com.teamlab.kotlin.mvvm.data.model.Account
import rx.mvvm.RxPropertyObservable
import rx.mvvm.ViewModel
import rx.mvvm.chain

class AccountItemViewModel(private val account: Account) : ViewModel() {
    val id = account.id
    val screenNameObservable = RxPropertyObservable.chain(account.screenNameObservable)
}
