package com.teamlab.kotlin.mvvm.di

import android.content.Context
import com.github.kubode.rxeventbus.RxEventBus
import com.teamlab.kotlin.mvvm.MyApplication
import dagger.Module
import dagger.Provides
import twitter4j.TwitterFactory
import javax.inject.Singleton

@Module
class MyApplicationModule(private val application: MyApplication) {

    private val CONSUMER_KEY = "hZOuxdz51CludNkjEyMuiR6JB"
    private val CONSUMER_SECRET = "2gtKvKETxmDlj0P9VNrzUQgm3JY6m8To1Trd5lqU8sOsPsyRMh"

    @Provides @Singleton fun provideMyApplication() = application
    @Provides @Singleton fun provideContext(): Context = application
    @Provides @Singleton fun provideRefWatcher() = application.ref
    @Provides @Singleton fun provideRxEventBus() = RxEventBus()
    @Provides fun provideTwitter() = TwitterFactory().instance.apply {
        setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET)
    }
}
