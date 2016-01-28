package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.model.AppPreferences
import rx.mvvm.RxPropertyObservable
import rx.mvvm.ViewModel
import rx.mvvm.chain
import javax.inject.Inject

class AccountsViewModel @Inject constructor(pref: AppPreferences) : ViewModel() {
    val accountsObservable = RxPropertyObservable.chain(pref.accountsObservable)
}
