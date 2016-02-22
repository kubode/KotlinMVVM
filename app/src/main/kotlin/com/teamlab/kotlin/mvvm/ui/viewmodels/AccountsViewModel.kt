package com.teamlab.kotlin.mvvm.ui.viewmodels

import com.teamlab.kotlin.mvvm.data.model.AppPreferences
import rx.mvvm.ViewModel
import rx.property.RxPropertyObservable
import rx.property.chain
import javax.inject.Inject

class AccountsViewModel @Inject constructor(pref: AppPreferences) : ViewModel() {
    val accountsObservable = RxPropertyObservable.chain(pref.accountsObservable)
}
