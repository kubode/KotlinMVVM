package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.observable
import rx.Observable
import rx.lang.kotlin.BehaviourSubject

class CategoryUpdateViewModel(id: Long) {
    private val category = Category.Manager.get(id).apply { status = Status.NORMAL }

    val nameObservable = BehaviourSubject<String>()
    val descriptionObservable = BehaviourSubject<String>()
    var statusObservable = category.statusObservable
    var errorObservable = category.errorObservable

    var name by observable(category.name, nameObservable)
    var description by observable(category.description, descriptionObservable)

    val nameValidationObservable = nameObservable
            .map {
                if (it.isEmpty()) {
                    "name is required."
                } else {
                    null
                }
            }
    val descriptionValidationObservable = descriptionObservable
            .map {
                if (it.isEmpty()) {
                    "description is required."
                } else {
                    null
                }
            }
    val updateEnabledObservable = Observable
            .combineLatest(nameValidationObservable, descriptionValidationObservable,
                    { v1, v2 -> (v1 == null && v2 == null) })

    fun update() {
        category.update(name, description)
    }

    fun delete() {
        category.delete()
    }
}
