package com.teamlab.kotlin.mvvm.view

import com.teamlab.kotlin.mvvm.ext.of
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.InjectionHierarchy
import com.teamlab.kotlin.mvvm.viewmodel.AccountsViewModel
import rx.mvvm.MvvmFragment

class AccountsFragment : MvvmFragment(), Injectable {
    override val injectionHierarchy = InjectionHierarchy.of(this)
    override lateinit var vm: AccountsViewModel

    override fun onInitializeViewModel() {
        vm = AccountsViewModel(activity)
    }
}
