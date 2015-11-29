package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.Cache
import com.teamlab.kotlin.mvvm.Model
import com.teamlab.kotlin.mvvm.observable
import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import java.util.concurrent.TimeUnit

class Category(id: Long) : Model<Long>() {

    override val id = id

    val nameObservable = BehaviourSubject<String>()
    val descriptionObservable = BehaviourSubject<String>()
    val statusObservable = BehaviourSubject<Status>()
    val errorObservable = BehaviourSubject<Throwable?>()

    var name by observable("", nameObservable)
    var description by observable("", descriptionObservable)
    var status by observable(Status.NORMAL, statusObservable)
    var error by observable(null, errorObservable)

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
