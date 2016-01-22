package com.teamlab.kotlin.mvvm.model

import rx.mvvm.Cache
import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.value

class User(account: Account, id: Long) : Model<Pair<Account, Long>>() {
    override val id = Pair(account, id)
    val nameObservable = RxPropertyObservable.value("")
    var name by nameObservable.toProperty()

    val Manager = object {
        private val cache = Cache<User, Pair<Account, Long>>()
    }
}
