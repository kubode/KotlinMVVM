package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.model.Categories
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.observable
import rx.Observable
import rx.lang.kotlin.BehaviourSubject

class CategoryAddViewModel {
    val idObservable = BehaviourSubject<String>()
    val nameObservable = BehaviourSubject<String>()
    val descriptionObservable = BehaviourSubject<String>()
    val statusObservable = BehaviourSubject<Status>()
    val errorObservable = BehaviourSubject<Throwable?>()

    var id by observable("", idObservable)
    var name by observable("", nameObservable)
    var description by observable("", descriptionObservable)
    var status by observable(Status.NORMAL, statusObservable)
    var error by observable(null, errorObservable)

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
