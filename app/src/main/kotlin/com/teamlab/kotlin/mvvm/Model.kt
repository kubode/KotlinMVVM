package com.teamlab.kotlin.mvvm

import rx.subjects.BehaviorSubject
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Model<K> {
    abstract val id: K

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Model<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}

fun <T> observable(behaviorSubject: BehaviorSubject<T>): ReadWriteProperty<Any?, T> {
    return object : ObservableProperty<T>(behaviorSubject.value) {
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            if (oldValue != newValue) {
                behaviorSubject.onNext(newValue)
            }
        }
    }
}
