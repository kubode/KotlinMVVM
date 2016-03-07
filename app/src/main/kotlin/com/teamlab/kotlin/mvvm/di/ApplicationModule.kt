package com.teamlab.kotlin.mvvm.di

import android.content.Context
import com.github.kubode.rxeventbus.RxEventBus
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso
import com.teamlab.kotlin.mvvm.BuildConfig
import com.teamlab.kotlin.mvvm.MyApplication
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val application: MyApplication) {

    @Provides @ApplicationScope fun provideMyApplication() = application
    @Provides @ApplicationScope fun provideContext(): Context = application
    @Provides @ApplicationScope fun provideRefWatcher() = application.ref
    @Provides @ApplicationScope fun provideRxEventBus() = RxEventBus()
    @Provides @ApplicationScope fun providePicasso() = Picasso.Builder(application)
            .indicatorsEnabled(BuildConfig.DEBUG)
            .loggingEnabled(BuildConfig.DEBUG)
            .downloader(OkHttp3Downloader(application))
            .memoryCache(LruCache(application))
            .build()
}
