package com.teamlab.kotlin.mvvm.viewmodel

import android.os.Bundle
import com.teamlab.kotlin.mvvm.ChainImmutableRxProperty
import com.teamlab.kotlin.mvvm.ValueMutableRxProperty
import com.teamlab.kotlin.mvvm.model.Categories
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import rx.Observable

class CategoryAddViewModel {
    val status = ValueMutableRxProperty(Status.NORMAL)
    val error = ValueMutableRxProperty<Throwable?>(null)
    val id = ValueMutableRxProperty("")
    val idValidation = ChainImmutableRxProperty(id.publishSubject
            .map {
                try {
                    it.toLong()
                    null
                } catch(e: NumberFormatException) {
                    "cant parse to long"
                }
            })

    val name = ValueMutableRxProperty("")
    val nameValidation = ChainImmutableRxProperty(name.behaviorSubject
            .map {
                if (it.isEmpty()) {
                    "name is required."
                } else {
                    null
                }
            })
    val description = ValueMutableRxProperty("")
    val descriptionValidation = ChainImmutableRxProperty(description.behaviorSubject
            .map {
                if (it.isEmpty()) {
                    "description is required."
                } else {
                    null
                }
            })
    val addEnabled = ChainImmutableRxProperty(Observable.combineLatest(
            idValidation.behaviorSubject,
            nameValidation.behaviorSubject,
            descriptionValidation.behaviorSubject,
            { v1, v2, v3 -> (v1 == null && v2 == null && v3 == null) }))

    constructor(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        id.value = savedInstanceState.getString("id")
        name.value = savedInstanceState.getString("name")
        description.value = savedInstanceState.getString("description")
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString("id", id.value)
        outState.putString("name", name.value)
        outState.putString("description", description.value)
    }

    fun add() {
        val category = Category(id.value.toLong())
        category.name.value = name.value
        category.description.value = description.value
        category.status.publishSubject.subscribe { status.value = it }
        category.error.publishSubject.subscribe { error.value = it }
        status.value = Status.REQUESTING
        Categories.Manager.add(category).subscribe({
            status.value = Status.COMPLETED
        }, {
            status.value = Status.ERROR
            error.value = it
        })
    }
}
