package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.ReadOnlyRxProperty
import com.teamlab.kotlin.mvvm.ViewModel
import com.teamlab.kotlin.mvvm.model.Categories
import com.teamlab.kotlin.mvvm.model.Status

class CategoriesViewModel(query: String) : ViewModel() {

    val categories = Categories.Manager.get(query)
    val list = ReadOnlyRxProperty(categories.list.value)
    val status = ReadOnlyRxProperty(categories.status.value)
    val error = ReadOnlyRxProperty(categories.error.value)
    val progressVisible = ReadOnlyRxProperty(false)
    val errorVisible = ReadOnlyRxProperty(false)

    init {
        bind(list, categories.list.behaviorSubject)
        bind(status, categories.status.behaviorSubject)
        bind(error, categories.error.behaviorSubject)
        bind(progressVisible, status.behaviorSubject.map { if (it == Status.REQUESTING) true else false })
        bind(errorVisible, status.behaviorSubject.map { if (it == Status.ERROR) true else false })
        categories.requestIfNotCompleted()
    }

    fun reload() {
        categories.requestIfNotCompleted()
    }
}
