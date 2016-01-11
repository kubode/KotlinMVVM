package rx.mvvm

import android.os.Bundle

abstract class ViewModel {
    fun restoreInstanceState(savedInstanceState: Bundle?) = savedInstanceState?.let { onRestoreInstanceState(it) }
    open protected fun onRestoreInstanceState(savedInstanceState: Bundle) = Unit
    fun saveInstanceState(outState: Bundle) = onSaveInstanceState(outState)
    open protected fun onSaveInstanceState(outState: Bundle) = Unit
    fun destroy() = onDestroy()
    open protected fun onDestroy() = Unit
}
