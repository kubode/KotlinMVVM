package com.teamlab.kotlin.mvvm.repository

import android.content.Context
import com.teamlab.kotlin.mvvm.di.ApplicationScope
import com.teamlab.kotlin.mvvm.ext.MyTwitterFactory
import com.teamlab.kotlin.mvvm.model.Account
import rx.mvvm.Cache
import javax.inject.Inject

@ApplicationScope
class AccountRepository @Inject constructor(private val context: Context,
                                            private val twitterFactory: MyTwitterFactory) {

    private val cache = Cache<Account, Long>()

    fun of(userId: Long): Account {
        return cache.getOrPut(userId, { Account(context, twitterFactory, userId) })
    }
}
