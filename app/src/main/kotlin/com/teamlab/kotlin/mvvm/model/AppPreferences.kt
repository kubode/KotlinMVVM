package com.teamlab.kotlin.mvvm.model

import android.content.Context
import com.teamlab.kotlin.mvvm.ext.TwitterFactory
import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.pref
import rx.mvvm.rxProperty

class AppPreferences(context: Context) : Model<Unit>() {
    private val SEPARATOR = ","

    override val id = Unit
    private val pref = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    val accountsObservable = RxPropertyObservable.pref<List<Account>>(
            pref,
            "accounts",
            emptyList(),
            { key, defValue ->
                getString(key, null)?.let {
                    it.split(SEPARATOR).map { Account(context, TwitterFactory.create(), it.toLong()) }
                } ?: defValue
            },
            { key, value ->
                putString(key, value.map { it.id.toString() }.joinToString(SEPARATOR))
            })
    var accounts by rxProperty(emptyList(), accountsObservable)
}
