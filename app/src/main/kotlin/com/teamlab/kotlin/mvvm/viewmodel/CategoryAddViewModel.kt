package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.model.Categories
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.observable
import rx.Observable
import rx.lang.kotlin.BehaviourSubject

class CategoryAddViewModel {
    val idObservable = BehaviourSubject("")
    val nameObservable = BehaviourSubject("")
    val descriptionObservable = BehaviourSubject("")
    val statusObservable = BehaviourSubject(Status.NORMAL)
    val errorObservable = BehaviourSubject<Throwable?>(null)

    var id: String by observable(idObservable)
    var name: String by observable(nameObservable)
    var description: String by observable(descriptionObservable)
    var status: Status by observable(statusObservable)
    var error: Throwable? by observable(errorObservable)

    var idValidationObservable = idObservable
            .map {
                try {
                    it.toLong()
                    null
                } catch(e: NumberFormatException) {
                    "cant parse to long"
                }
            }
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
    val addEnabledObservable = Observable
            .combineLatest(idValidationObservable, nameValidationObservable, descriptionValidationObservable,
                    { v1, v2, v3 -> (v1 == null && v2 == null && v3 == null) })

    fun add() {
        val category = Category(id.toLong())
        category.name = name
        category.description = description
        category.statusObservable.subscribe { status = it }
        category.errorObservable.subscribe { error = it }
        status = Status.REQUESTING
        Categories.Manager.add(category).subscribe({
            status = Status.COMPLETED
        }, {
            status = Status.ERROR
            error = it
        })
    }
}
