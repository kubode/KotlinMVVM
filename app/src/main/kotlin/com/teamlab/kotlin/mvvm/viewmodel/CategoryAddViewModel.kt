package com.teamlab.kotlin.mvvm.viewmodel

import android.os.Bundle
import com.teamlab.kotlin.mvvm.ReadOnlyRxProperty
import com.teamlab.kotlin.mvvm.RxProperty
import com.teamlab.kotlin.mvvm.ViewModel
import com.teamlab.kotlin.mvvm.model.Categories
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import rx.Observable

class CategoryAddViewModel : ViewModel() {
    val status = RxProperty(Status.NORMAL)
    val error = RxProperty<Throwable?>(null)
    val id = RxProperty<CharSequence>("")
    val name = RxProperty<CharSequence>("")
    val description = RxProperty<CharSequence>("")

    val idValidation = ReadOnlyRxProperty(id.value)
    val nameValidation = ReadOnlyRxProperty(name.value)
    val descriptionValidation = ReadOnlyRxProperty(description.value)
    val addEnabled = ReadOnlyRxProperty(false)
    val progressVisible = ReadOnlyRxProperty(false)

    init {
        bind(idValidation, id.behaviorSubject
                .map { it.toString().matches("[0-9]+".toRegex()) }
                .map { if (it) "" else "cant parse to long" })
        bind(nameValidation, name.behaviorSubject
                .map { if (it.isEmpty()) "name is required." else "" })
        bind(descriptionValidation, description.behaviorSubject
                .map { if (it.isEmpty()) "description is required." else "" })
        bind(addEnabled, Observable.combineLatest(
                idValidation.behaviorSubject,
                nameValidation.behaviorSubject,
                descriptionValidation.behaviorSubject,
                { v1, v2, v3 -> v1.isEmpty() && v2.isEmpty() && v3.isEmpty() }))
        bind(progressVisible, status.behaviorSubject.map { it == Status.REQUESTING })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        id.value = savedInstanceState.getCharSequence("id")
        name.value = savedInstanceState.getCharSequence("name")
        description.value = savedInstanceState.getCharSequence("description")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence("id", id.value)
        outState.putCharSequence("name", name.value)
        outState.putCharSequence("description", description.value)
    }

    fun add() {
        val category = Category(id.value.toString().toLong())
        category.name.value = name.value.toString()
        category.description.value = description.value.toString()
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
