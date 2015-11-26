package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.model.Categories

class CategoriesViewModel(query: String) {
    private val categories = Categories.Manager.get(query)

    val listObservable = categories.listObservable
    val statusObservable = categories.statusObservable
    val errorObservable = categories.errorObservable

    init {
        categories.requestIfNotCompleted()
    }

    fun reload() {
        categories.requestIfNotCompleted()
    }
}
