package rx.mvvm

import android.os.Bundle

abstract class ViewModel {
    fun performRestoreInstanceState(savedInstanceState: Bundle?) = savedInstanceState?.let { onRestoreInstanceState(it) }
    open protected fun onRestoreInstanceState(savedInstanceState: Bundle) = Unit
    fun performSaveInstanceState(outState: Bundle) = onSaveInstanceState(outState)
    open protected fun onSaveInstanceState(outState: Bundle) = Unit
    fun performDestroy() = onDestroy()
    open protected fun onDestroy() = Unit
}
