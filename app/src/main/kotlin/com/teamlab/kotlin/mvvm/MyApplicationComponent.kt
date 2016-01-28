package com.teamlab.kotlin.mvvm

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.teamlab.kotlin.mvvm.view.AccountsFragment
import com.teamlab.kotlin.mvvm.view.AddAccountDialogFragment
import com.teamlab.kotlin.mvvm.view.MainActivity
import dagger.Component
import twitter4j.Twitter
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(MyApplicationModule::class))
interface MyApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(accountsFragment: AccountsFragment)
    fun inject(addAccountDialogFragment: AddAccountDialogFragment)

    fun twitter(): Twitter

    companion object {
        fun from(context: Context) = (context.applicationContext as MyApplication).component
        fun from(activity: Activity) = from(activity as Context)
        fun from(fragment: Fragment) = from(fragment.activity)
    }
}
