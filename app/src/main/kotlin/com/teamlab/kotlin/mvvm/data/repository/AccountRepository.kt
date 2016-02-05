package com.teamlab.kotlin.mvvm.data.repository

import android.content.Context
import com.teamlab.kotlin.mvvm.di.ApplicationScope
import com.teamlab.kotlin.mvvm.data.factory.MyTwitterFactory
import com.teamlab.kotlin.mvvm.data.model.Account
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
