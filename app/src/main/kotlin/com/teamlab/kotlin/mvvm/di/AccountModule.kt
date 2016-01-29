package com.teamlab.kotlin.mvvm.di

import com.teamlab.kotlin.mvvm.model.Account
import dagger.Module
import dagger.Provides

@Module
// https://youtrack.jetbrains.com/issue/KT-9804
class AccountModule(private val account: Account) {
    @Provides @AccountScope fun provideAccountAccountScope() = account
    @Provides @AccountScope fun provideContextAccountScope() = account.context
    @Provides @AccountScope fun provideTwitterAccountScope() = account.twitter
}
