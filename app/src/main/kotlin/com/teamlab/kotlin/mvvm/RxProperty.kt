package com.teamlab.kotlin.mvvm

import rx.Observable
import rx.Subscription
import rx.subjects.BehaviorSubject

open class ReadOnlyRxProperty<T>(initialValue: T) {
    private val behaviorSubjectInternal = BehaviorSubject.create(initialValue)
    open var value: T
        get() = behaviorSubjectInternal.value
        protected set(value) = behaviorSubjectInternal.onNext(value)
    val behaviorSubject = behaviorSubjectInternal.asObservable()
    val publishSubject = behaviorSubjectInternal.skip(1)
    fun <X : T> bind(observable: Observable<X>): Subscription {
        return observable.subscribe { value = it }
    }

    override fun toString(): String {
        return "$value"
    }
}

class RxProperty<T>(initialValue: T) : ReadOnlyRxProperty<T>(initialValue) {
    override var value: T
        get() = super.value
        public set(value) {
            super.value = value
        }
}
