package rx.mvvm

import android.os.Bundle
import rx.subscriptions.CompositeSubscription

abstract class ViewModel {

    private val subscription = CompositeSubscription()

    fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        onRestoreInstanceState(savedInstanceState)
    }

    open protected fun onRestoreInstanceState(savedInstanceState: Bundle) = Unit

    fun saveInstanceState(outState: Bundle) {
        onSaveInstanceState(outState)
    }

    open protected fun onSaveInstanceState(outState: Bundle) = Unit

    fun destroy() {
        subscription.unsubscribe()
        onDestroy()
    }

    open protected fun onDestroy() = Unit
}
