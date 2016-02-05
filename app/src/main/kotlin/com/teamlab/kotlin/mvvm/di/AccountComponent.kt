package com.teamlab.kotlin.mvvm.di

import android.app.Activity
import com.teamlab.kotlin.mvvm.model.Timeline
import com.teamlab.kotlin.mvvm.model.Tweet
import com.teamlab.kotlin.mvvm.view.AccountActivity
import com.teamlab.kotlin.mvvm.view.TimelineFragment
import dagger.Component

@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(AccountModule::class))
@AccountScope
interface AccountComponent {
    fun inject(tweet: Tweet)
    fun inject(timeline: Timeline)
    fun inject(timelineFragment: TimelineFragment)

    companion object {
        fun from(activity: Activity): AccountComponent {
            return (activity as AccountActivity).account.component
        }
    }
}
