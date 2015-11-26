package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.Cache
import com.teamlab.kotlin.mvvm.Model
import com.teamlab.kotlin.mvvm.observable
import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import java.util.concurrent.TimeUnit

class Category(id: Long) : Model<Long>() {

    override val id = id

    val nameObservable = BehaviourSubject("")
    val descriptionObservable = BehaviourSubject("")
    val statusObservable = BehaviourSubject(Status.NORMAL)
    val errorObservable = BehaviourSubject<Throwable?>(null)

    var name: String by observable(nameObservable)
    var description: String by observable(descriptionObservable)
    var status: Status by observable(statusObservable)
    var error: Throwable? by observable(errorObservable)

    override fun toString(): String {
        return "id: $id, name: $name, description: $description, status: $status, error: $error"
    }

    fun update(name: String, description: String) {
        status = Status.REQUESTING
        error = null
        Observable.just(this)
                .delay(1, TimeUnit.SECONDS)
                .subscribe({
                    // Check duplicated name
                    val category = it
                    Categories.Manager.cache.getAll().forEach {
                        if (it.list.find { it != category && it.name == category.name } != null) {
                            throw RuntimeException("$category is already exists.")
                        }
                    }
                    it.status = Status.COMPLETED
                    it.name = name
                    it.description = description
                }, {
                    status = Status.ERROR
                    error = it
                })
    }

    fun delete() {
        status = Status.REQUESTING
        error = null
        Observable.just(this)
                .delay(1, TimeUnit.SECONDS)
                .subscribe({
                    // Remove from Categories
                    val category = it
                    Categories.Manager.cache.getAll().forEach {
                        it.list -= category
                    }
                    status = Status.COMPLETED
                }, {
                    status = Status.ERROR
                    error = it
                })
    }

    object Manager {
        val cache = Cache<Category, Long>()

        fun get(id: Long): Category {
            return cache.get(id) ?: Category(id).apply { cache.put(this) }
        }
    }
}
