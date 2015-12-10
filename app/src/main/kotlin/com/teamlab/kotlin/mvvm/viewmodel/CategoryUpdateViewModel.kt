package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.ChainImmutableRxProperty
import com.teamlab.kotlin.mvvm.ValueMutableRxProperty
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import rx.Observable

class CategoryUpdateViewModel(id: Long) {
    val category = Category.Manager.get(id).apply { status.value = Status.NORMAL }
    val status = ChainImmutableRxProperty(category.status.behaviorSubject)
    val error = ChainImmutableRxProperty(category.error.behaviorSubject)
    val name = ValueMutableRxProperty(category.name.value)
    val nameValidation = ChainImmutableRxProperty(name.behaviorSubject
            .map {
                if (it.isEmpty()) {
                    "name is required."
                } else {
                    null
                }
            })
    val description = ValueMutableRxProperty(category.description.value)
    val descriptionValidation = ChainImmutableRxProperty(description.behaviorSubject
            .map {
                if (it.isEmpty()) {
                    "description is required."
                } else {
                    null
                }
            })
    val updateEnabled = ChainImmutableRxProperty(Observable
            .combineLatest(nameValidation.behaviorSubject, descriptionValidation.behaviorSubject,
                    { v1, v2 -> (v1 == null && v2 == null) }))

    fun update() {
        category.update(name.value, description.value)
    }

    fun delete() {
        category.delete()
    }
}
