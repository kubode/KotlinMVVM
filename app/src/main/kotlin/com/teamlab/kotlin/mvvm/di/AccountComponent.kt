package com.teamlab.kotlin.mvvm.di

import com.teamlab.kotlin.mvvm.model.Tweet
import dagger.Component

@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(AccountModule::class))
@AccountScope
interface AccountComponent {
    fun inject(tweet: Tweet)
}
