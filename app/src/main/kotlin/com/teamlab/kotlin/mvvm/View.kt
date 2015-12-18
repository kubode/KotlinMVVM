package com.teamlab.kotlin.mvvm

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment

abstract class MvvmFragment : Fragment() {

    protected abstract val vm: ViewModel

    protected abstract fun onCreateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateViewModel()
        vm.restoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vm.saveInstanceState(outState)
    }

    override fun onDestroy() {
        vm.destroy()
        super.onDestroy()
    }
}

abstract class MvvmDialogFragment : DialogFragment() {

    protected abstract val vm: ViewModel

    protected abstract fun onCreateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateViewModel()
        vm.restoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vm.saveInstanceState(outState)
    }

    override fun onDestroy() {
        vm.destroy()
        super.onDestroy()
    }
}
