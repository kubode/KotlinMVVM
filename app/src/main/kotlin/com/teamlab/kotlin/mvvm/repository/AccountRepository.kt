package com.teamlab.kotlin.mvvm.repository

import android.content.Context
import com.teamlab.kotlin.mvvm.ext.MyTwitterFactory
import com.teamlab.kotlin.mvvm.model.Account
import rx.mvvm.Cache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val context: Context,
                                            private val twitterFactory: MyTwitterFactory) {

    private val cache = Cache<Account, Long>()

    fun of(userId: Long): Account {
        return cache.getAndPut(userId, { Account(context, twitterFactory.create(), userId) })
    }
}
