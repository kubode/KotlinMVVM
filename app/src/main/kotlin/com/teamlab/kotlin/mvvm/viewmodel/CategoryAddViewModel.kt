package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.MutableObservableProperty
import com.teamlab.kotlin.mvvm.ObservableChainProperty
import com.teamlab.kotlin.mvvm.model.Categories
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import rx.Observable

class CategoryAddViewModel {
    val status = MutableObservableProperty(Status.NORMAL)
    val error = MutableObservableProperty<Throwable?>(null)
    val id = MutableObservableProperty("")
    val idValidation = ObservableChainProperty(id.observable
            .map {
                try {
                    it.toLong()
                    null
                } catch(e: NumberFormatException) {
                    "cant parse to long"
                }
            })

    val name = MutableObservableProperty("")
    val nameValidation = ObservableChainProperty(name.observable
            .map {
                if (it.isEmpty()) {
                    "name is required."
                } else {
                    null
                }
            })
    val description = MutableObservableProperty("")
    val descriptionValidation = ObservableChainProperty(description.observable
            .map {
                if (it.isEmpty()) {
                    "description is required."
                } else {
                    null
                }
            })
    val addEnabled = ObservableChainProperty(Observable
            .combineLatest(idValidation.observable, nameValidation.observable, descriptionValidation.observable,
                    { v1, v2, v3 -> (v1 == null && v2 == null && v3 == null) }))

    fun add() {
        val category = Category(id.value.toLong())
        category.name.value = name.value
        category.description.value = description.value
        category.status.observable.subscribe { status.value = it }
        category.error.observable.subscribe { error.value = it }
        status.value = Status.REQUESTING
        Categories.Manager.add(category).subscribe({
            status.value = Status.COMPLETED
        }, {
            status.value = Status.ERROR
            error.value = it
        })
    }
}
