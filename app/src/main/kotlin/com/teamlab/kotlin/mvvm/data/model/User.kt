package com.teamlab.kotlin.mvvm.data.model

import rx.mvvm.Model
import rx.property.RxPropertyObservable
import rx.property.value

class User(account: Account, user: twitter4j.User) : Model<Long>() {
    override val id = user.id

    val nameObservable = RxPropertyObservable.value("")
    private var name by nameObservable.toProperty()
    val profileImageUrlObservable = RxPropertyObservable.value("")
    private var profileImageUrl by profileImageUrlObservable.toProperty()

    fun merge(user: twitter4j.User) {
        name = user.name
        profileImageUrl = user.profileImageURL
    }
}
