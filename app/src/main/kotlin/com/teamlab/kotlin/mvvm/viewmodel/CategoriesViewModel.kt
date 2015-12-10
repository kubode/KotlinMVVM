package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.ChainImmutableRxProperty
import com.teamlab.kotlin.mvvm.model.Categories

class CategoriesViewModel(query: String) {

    val categories = Categories.Manager.get(query)
    val list = ChainImmutableRxProperty(categories.list.behaviorSubject)
    val status = ChainImmutableRxProperty(categories.status.behaviorSubject)
    val error = ChainImmutableRxProperty(categories.error.behaviorSubject)

    init {
        categories.requestIfNotCompleted()
    }

    fun reload() {
        categories.requestIfNotCompleted()
    }
}
