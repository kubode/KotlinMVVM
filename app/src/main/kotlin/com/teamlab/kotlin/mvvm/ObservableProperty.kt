package com.teamlab.kotlin.mvvm

import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import kotlin.properties.Delegates

interface ObservableProperty<T> {
    val observable: Observable<T>
}

class MutableObservableProperty<T>(initialValue: T) : ObservableProperty<T> {
    private val subject = BehaviourSubject(initialValue)
    var value: T by Delegates.observable(initialValue) { property, old, new ->
        if (old != new) {
            subject.onNext(new)
        }
    }
    override val observable: Observable<T> = subject

    override fun toString(): String {
        return "$value"
    }
}

class ObservableChainProperty<T>(override val observable: Observable<T>) : ObservableProperty<T>
