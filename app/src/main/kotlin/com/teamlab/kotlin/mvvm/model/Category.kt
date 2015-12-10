package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.Cache
import com.teamlab.kotlin.mvvm.Model
import com.teamlab.kotlin.mvvm.ValueMutableRxProperty
import rx.Observable
import java.util.concurrent.TimeUnit

class Category(id: Long) : Model<Long>(id) {
    val name = ValueMutableRxProperty("")
    val description = ValueMutableRxProperty("")
    val status = ValueMutableRxProperty(Status.NORMAL)
    val error = ValueMutableRxProperty(null as Throwable?)

    override fun toString(): String {
        return "id: $id, name: $name, description: $description, status: $status, error: $error"
    }

    fun update(name: String, description: String) {
        status.value = Status.REQUESTING
        error.value = null
        Observable.just(this)
                .delay(1, TimeUnit.SECONDS)
                .subscribe({
                    // Check duplicated name
                    val category = it
                    Categories.Manager.cache.getAll().forEach {
                        if (it.list.value.find { it != category && it.name.value == category.name.value } != null) {
                            throw RuntimeException("$category is already exists.")
                        }
                    }
                    it.status.value = Status.COMPLETED
                    it.name.value = name
                    it.description.value = description
                }, {
                    status.value = Status.ERROR
                    error.value = it
                })
    }

    fun delete() {
        status.value = Status.REQUESTING
        error.value = null
        Observable.just(this)
                .delay(1, TimeUnit.SECONDS)
                .subscribe({
                    // Remove from Categories
                    val category = it
                    Categories.Manager.cache.getAll().forEach {
                        val categories = it.list
                        if (category in categories.value) {
                            categories.value -= category
                        }
                    }
                    status.value = Status.COMPLETED
                }, {
                    status.value = Status.ERROR
                    error.value = it
                })
    }

    object Manager {
        val cache = Cache<Category, Long>()

        fun get(id: Long): Category {
            return cache.get(id) ?: Category(id).apply { cache.put(this) }
        }
    }
}
