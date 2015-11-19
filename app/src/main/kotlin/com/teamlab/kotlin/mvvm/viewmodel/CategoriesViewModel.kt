package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.MutableObservableProperty
import com.teamlab.kotlin.mvvm.ObservableChainProperty
import com.teamlab.kotlin.mvvm.model.Category

class CategoriesViewModel {
    val status = MutableObservableProperty(Status.LOADING)
    val error = MutableObservableProperty<Throwable?>(null)
    val categories = ObservableChainProperty(Category.Manager.categories.observable)

    fun loadIfNotInitialized() {
        if (status.value == Status.INITIALIZED) {
            return
        }
        status.value = Status.LOADING
        error.value = null
        Category.Manager.queryAll()
                .subscribe({
                    status.value = Status.INITIALIZED
                }, {
                    status.value = Status.ERROR
                    error.value = it
                })
    }

    enum class Status {
        LOADING, ERROR, INITIALIZED
    }
}
