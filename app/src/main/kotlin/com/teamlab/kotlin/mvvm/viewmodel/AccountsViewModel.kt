package com.teamlab.kotlin.mvvm.viewmodel

import android.content.Context
import com.teamlab.kotlin.mvvm.ext.of
import com.teamlab.kotlin.mvvm.model.AppPreferences
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.InjectionHierarchy
import com.teamlab.kotlin.mvvm.util.inject
import rx.mvvm.RxPropertyObservable
import rx.mvvm.ViewModel
import rx.mvvm.chain

class AccountsViewModel(context: Context) : ViewModel(), Injectable {
    override val injectionHierarchy = InjectionHierarchy.of(context)

    private val pref by inject(AppPreferences::class)
    val accountsObservable = RxPropertyObservable.chain(pref.accountsObservable)
}
