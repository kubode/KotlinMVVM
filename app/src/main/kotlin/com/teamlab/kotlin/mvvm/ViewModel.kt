package com.teamlab.kotlin.mvvm

import android.os.Bundle
import rx.Observable
import rx.subscriptions.CompositeSubscription

abstract class ViewModel {

    private val subscription = CompositeSubscription()

    fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        onRestoreInstanceState(savedInstanceState)
    }

    open protected fun onRestoreInstanceState(savedInstanceState: Bundle) {
    }

    fun saveInstanceState(outState: Bundle) {
        onSaveInstanceState(outState)
    }

    open protected fun onSaveInstanceState(outState: Bundle) {
    }

    protected fun <T> bind(property: ReadOnlyRxProperty<T>, observable: Observable<out T>) {
        subscription.add(property.bind(observable))
    }

    fun destroy() {
        subscription.unsubscribe()
        onDestroy()
    }

    open protected fun onDestroy() {
    }
}
