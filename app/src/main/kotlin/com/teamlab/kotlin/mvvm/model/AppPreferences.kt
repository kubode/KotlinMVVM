package com.teamlab.kotlin.mvvm.model

import android.content.Context
import com.teamlab.kotlin.mvvm.util.logV
import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.pref
import rx.mvvm.rxProperty
import twitter4j.auth.AccessToken

class AppPreferences(private val context: Context) : Model<Unit>() {
    private val SEPARATOR = ","

    override val id = Unit
    private val pref = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    val accountsObservable = RxPropertyObservable.pref<List<Account>>(
            pref,
            "accounts",
            emptyList(),
            { key, defValue ->
                logV({"$key, ${getString(key, null)}"})
                getString(key, null).let {
                    if (it.isNullOrEmpty()) null else it
                }?.let {
                    it.split(SEPARATOR).map { Account.of(context, it.toLong()) }
                } ?: defValue
            },
            { key, value ->
                putString(key, value.map { it.id.toString() }.joinToString(SEPARATOR))
            })
    var accounts by rxProperty(emptyList(), accountsObservable)

    fun addAccount(token: AccessToken): Account {
        val account = Account.of(context, token.userId)
        if (account in accounts) throw RuntimeException("$account は登録済みのアカウントです。")
        account.initialize(token)
        accounts += account
        return account
    }
}
