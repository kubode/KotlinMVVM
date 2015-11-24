package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.MutableObservableProperty
import com.teamlab.kotlin.mvvm.ObservableChainProperty
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import rx.Observable

class CategoryEditViewModel(id: Long) {
    val category = Category.Manager.get(id).apply { status.value = Status.NORMAL }
    val status = ObservableChainProperty(category.status.observable)
    val error = ObservableChainProperty(category.error.observable)
    val name = MutableObservableProperty(category.name.value)
    val nameValidation = ObservableChainProperty(name.observable
            .map {
                if (it.isEmpty()) {
                    "name is required."
                } else {
                    null
                }
            })
    val description = MutableObservableProperty(category.description.value)
    val descriptionValidation = ObservableChainProperty(description.observable
            .map {
                if (it.isEmpty()) {
                    "description is required."
                } else {
                    null
                }
            })
    val updateEnabled = ObservableChainProperty(Observable
            .combineLatest(nameValidation.observable, descriptionValidation.observable,
                    { v1, v2 -> (v1 == null && v2 == null) }))

    fun update() {
        category.update(name.value, description.value)
    }

    fun delete() {
        category.delete()
    }
}
