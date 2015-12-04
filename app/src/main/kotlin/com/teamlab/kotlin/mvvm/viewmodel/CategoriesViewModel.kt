package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.ObservableChainProperty
import com.teamlab.kotlin.mvvm.model.Categories

class CategoriesViewModel(query: String) {

    val categories = Categories.Manager.get(query)
    val list = ObservableChainProperty(categories.list.observable)
    val status = ObservableChainProperty(categories.status.observable)
    val error = ObservableChainProperty(categories.error.observable)

    init {
        categories.requestIfNotCompleted()
    }

    fun reload() {
        categories.requestIfNotCompleted()
    }
}
