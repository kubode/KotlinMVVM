package com.teamlab.kotlin.mvvm.di

import android.content.Context
import com.github.kubode.rxeventbus.RxEventBus
import com.teamlab.kotlin.mvvm.MyApplication
import dagger.Module
import dagger.Provides
import twitter4j.TwitterFactory

@Module
class ApplicationModule(private val application: MyApplication) {

    private val CONSUMER_KEY = "hZOuxdz51CludNkjEyMuiR6JB"
    private val CONSUMER_SECRET = "2gtKvKETxmDlj0P9VNrzUQgm3JY6m8To1Trd5lqU8sOsPsyRMh"

    @Provides @ApplicationScope fun provideMyApplication() = application
    @Provides @ApplicationScope fun provideContext(): Context = application
    @Provides @ApplicationScope fun provideRefWatcher() = application.ref
    @Provides @ApplicationScope fun provideRxEventBus() = RxEventBus()
    @Provides fun provideTwitter() = TwitterFactory().instance.apply {
        setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET)
    }
}
