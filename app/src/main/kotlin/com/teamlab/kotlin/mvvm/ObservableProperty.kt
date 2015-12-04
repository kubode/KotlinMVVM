package com.teamlab.kotlin.mvvm

import rx.Observable
import rx.subjects.BehaviorSubject
import kotlin.properties.Delegates

interface ObservableProperty<T> {
    val observable: Observable<T>
}

class MutableObservableProperty<T>(initialValue: T) : ObservableProperty<T> {
    private val behaviorSubject = BehaviorSubject.create(initialValue)
    override val observable: Observable<T> = behaviorSubject
    var value by Delegates.observable(initialValue) { property, old, new -> if (old != new) behaviorSubject.onNext(new) }
    override fun toString() = "$value"
}

class ObservableChainProperty<T>(override val observable: Observable<T>) : ObservableProperty<T>
