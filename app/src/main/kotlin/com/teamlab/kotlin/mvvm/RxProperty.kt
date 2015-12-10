package com.teamlab.kotlin.mvvm

import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import kotlin.properties.Delegates

interface RxProperty<T> {
    val value: T
    val publishSubject: Observable<T>
    val behaviorSubject: Observable<T>
}

interface ImmutableRxProperty<T> : RxProperty<T>

interface MutableRxProperty<T> : RxProperty<T> {
    override var value: T
}

class ValueImmutableRxProperty<T>(initialValue: T) : ImmutableRxProperty<T> {
    override val value = initialValue
    override val publishSubject = PublishSubject.create<T>().asObservable()
    override val behaviorSubject = BehaviorSubject.create(initialValue).asObservable()
    override fun toString() = "$value"
}

class ValueMutableRxProperty<T>(initialValue: T) : MutableRxProperty<T> {
    private val publishSubjectInternal = PublishSubject.create<T>()
    private val behaviorSubjectInternal = BehaviorSubject.create(initialValue)
    override var value by Delegates.observable(initialValue) { property, old, new ->
        if (old != new) {
            publishSubjectInternal.onNext(new)
            behaviorSubjectInternal.onNext(new)
        }
    }
    override val publishSubject = publishSubjectInternal.asObservable()
    override val behaviorSubject = behaviorSubjectInternal.asObservable()
    override fun toString() = "$value"
}

class ChainImmutableRxProperty<T>(private val behaviorSubjectChain: Observable<T>) : ImmutableRxProperty<T> {
    override val value: T
        get() = behaviorSubjectChain.toBlocking().first()
    override val publishSubject: Observable<T>
        get() = behaviorSubjectChain.skip(1)
    override val behaviorSubject: Observable<T>
        get() = behaviorSubjectChain

    override fun toString() = "$value"
}

class ChainMutableRxProperty<T>(private val behaviorSubjectChain: Observable<MutableRxProperty<T>>) : MutableRxProperty<T> {
    override var value: T
        get() = behaviorSubjectChain.toBlocking().first().value
        set(value) {
            behaviorSubjectChain.toBlocking().first().value = value
        }
    override val publishSubject: Observable<T>
        get() = behaviorSubjectChain.switchMap { it.publishSubject }
    override val behaviorSubject: Observable<T>
        get() = behaviorSubjectChain.switchMap { it.behaviorSubject }

    override fun toString() = "$value"
}
