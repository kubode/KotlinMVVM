package com.teamlab.kotlin.mvvm.di

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.github.kubode.rxeventbus.RxEventBus
import com.teamlab.kotlin.mvvm.MyApplication
import com.teamlab.kotlin.mvvm.ui.view.AccountsFragment
import com.teamlab.kotlin.mvvm.ui.view.AddAccountDialogFragment
import com.teamlab.kotlin.mvvm.ui.view.MainActivity
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
@ApplicationScope
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(accountActivity: com.teamlab.kotlin.mvvm.ui.view.AccountActivity)
    fun inject(accountsFragment: AccountsFragment)
    fun inject(addAccountDialogFragment: AddAccountDialogFragment)

    val rxEventBus: RxEventBus

    companion object {
        fun from(context: Context) = (context.applicationContext as MyApplication).component
        fun from(activity: Activity) = from(activity as Context)
        fun from(fragment: Fragment) = from(fragment.activity)
    }
}
