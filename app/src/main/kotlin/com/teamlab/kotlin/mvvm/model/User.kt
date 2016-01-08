package com.teamlab.kotlin.mvvm.model

import rx.mvvm.Model
import rx.mvvm.RxPropertyObservable
import rx.mvvm.rxProperty
import rx.mvvm.value

class User(account: Account, id: Long) : Model<Pair<Account, Long>>() {
    override val id = Pair(account, id)
    val nameObservable = RxPropertyObservable.value<String>()
    var name by rxProperty("", nameObservable)
}
