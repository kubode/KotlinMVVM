package com.teamlab.kotlin.mvvm.di

import android.content.Context
import com.github.kubode.rxeventbus.RxEventBus
import com.teamlab.kotlin.mvvm.MyApplication
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val application: MyApplication) {

    @Provides @ApplicationScope fun provideMyApplication() = application
    @Provides @ApplicationScope fun provideContext(): Context = application
    @Provides @ApplicationScope fun provideRefWatcher() = application.ref
    @Provides @ApplicationScope fun provideRxEventBus() = RxEventBus()
}
