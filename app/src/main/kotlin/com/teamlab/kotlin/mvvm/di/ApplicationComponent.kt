package com.teamlab.kotlin.mvvm.di

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.github.kubode.rxeventbus.RxEventBus
import com.squareup.picasso.Picasso
import com.teamlab.kotlin.mvvm.MyApplication
import com.teamlab.kotlin.mvvm.data.repository.ModelCache
import com.teamlab.kotlin.mvvm.ui.views.AccountActivity
import com.teamlab.kotlin.mvvm.ui.views.AccountsFragment
import com.teamlab.kotlin.mvvm.ui.views.AddAccountDialogFragment
import com.teamlab.kotlin.mvvm.ui.views.MainActivity
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
@ApplicationScope
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(accountActivity: AccountActivity)
    fun inject(accountsFragment: AccountsFragment)
    fun inject(addAccountDialogFragment: AddAccountDialogFragment)

    val rxEventBus: RxEventBus
    val modelCache: ModelCache
    val picasso: Picasso

    companion object {
        fun from(context: Context) = (context.applicationContext as MyApplication).component
        fun from(activity: Activity) = from(activity as Context)
        fun from(fragment: Fragment) = from(fragment.activity)
    }
}
