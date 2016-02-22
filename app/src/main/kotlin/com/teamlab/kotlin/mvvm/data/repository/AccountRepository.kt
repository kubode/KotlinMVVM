package com.teamlab.kotlin.mvvm.data.repository

import android.content.Context
import com.teamlab.kotlin.mvvm.data.factory.MyTwitterFactory
import com.teamlab.kotlin.mvvm.data.model.Account
import com.teamlab.kotlin.mvvm.di.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class AccountRepository @Inject constructor(private val context: Context,
                                            private val twitterFactory: MyTwitterFactory,
                                            private val cache: ModelCache) {

    fun of(userId: Long): Account {
        return cache.getOrPut(Account::class, userId, { Account(context, twitterFactory, it) })
    }
}
