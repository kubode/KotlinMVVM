package com.teamlab.kotlin.mvvm.viewmodel

import android.os.Bundle
import com.teamlab.kotlin.mvvm.ReadOnlyRxProperty
import com.teamlab.kotlin.mvvm.RxProperty
import com.teamlab.kotlin.mvvm.ViewModel
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import rx.Observable

class CategoryUpdateViewModel(id: Long) : ViewModel() {
    val category = Category.Manager.get(id).apply { status.value = Status.NORMAL }
    val status = ReadOnlyRxProperty(category.status.value)
    val error = ReadOnlyRxProperty(category.error.value)
    val name = RxProperty<CharSequence>(category.name.value)
    val description = RxProperty<CharSequence>(category.description.value)
    val nameValidation = ReadOnlyRxProperty(name.value)
    val descriptionValidation = ReadOnlyRxProperty(description.value)
    val progressVisible = ReadOnlyRxProperty(false)
    val updateEnabled = ReadOnlyRxProperty(false)

    init {
        bind(status, category.status.behaviorSubject)
        bind(error, category.error.behaviorSubject)
        bind(nameValidation, name.behaviorSubject.map {
            if (it.isEmpty()) "name is required." else ""
        })
        bind(descriptionValidation, description.behaviorSubject.map {
            if (it.isEmpty()) "description is required." else ""
        })
        bind(progressVisible, status.behaviorSubject.map { if (it == Status.REQUESTING) true else false })
        bind(updateEnabled, Observable.combineLatest(
                nameValidation.behaviorSubject,
                descriptionValidation.behaviorSubject,
                { v1, v2 -> v1.isEmpty() && v2.isEmpty() }))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        name.value = savedInstanceState.getCharSequence("name")
        description.value = savedInstanceState.getCharSequence("description")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence("name", name.value)
        outState.putCharSequence("description", description.value)
    }

    fun update() {
        category.update(name.value.toString(), description.value.toString())
    }

    fun delete() {
        category.delete()
    }
}
