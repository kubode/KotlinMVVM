package com.teamlab.kotlin.mvvm.viewmodel

import com.teamlab.kotlin.mvvm.MutableObservableProperty
import com.teamlab.kotlin.mvvm.ObservableChainProperty
import com.teamlab.kotlin.mvvm.model.Category
import rx.Observable

class AddCategoryViewModel {
    val status = MutableObservableProperty(Status.NORMAL)
    val error = MutableObservableProperty<Throwable?>(null)
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
    val submitEnabled = ObservableChainProperty(Observable
            .combineLatest(nameValidation.observable, descriptionValidation.observable,
                    { v1, v2 -> (v1 == null && v2 == null) }))

    fun post() {
        status.value = Status.POSTING
        Category.Manager.add(Category(name.value, description.value))
                .subscribe({
                    status.value = Status.FINISHED
                }, {
                    status.value = Status.ERROR
                    error.value = it
                })
    }

    enum class Status {
        NORMAL, POSTING, ERROR, FINISHED
    }
}
