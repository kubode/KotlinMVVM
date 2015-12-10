package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.Cache
import com.teamlab.kotlin.mvvm.Model
import com.teamlab.kotlin.mvvm.ValueMutableRxProperty
import rx.Observable
import java.util.concurrent.TimeUnit

class Categories(query: String) : Model<String>(query) {
    val list = ValueMutableRxProperty(emptyList<Category>())
    val status = ValueMutableRxProperty(Status.NORMAL)
    val error = ValueMutableRxProperty(null as Throwable?)

    fun requestIfNotCompleted() {
        if (status.value == Status.COMPLETED) {
            return
        }
        val failForDebug = status.value != Status.ERROR // DEBUG
        status.value = Status.REQUESTING
        error.value = null
        Observable.range(0, 5)
                .map { Category.Manager.get(it.toLong()) }
                .doOnNext {
                    it.name.value = "Category ${it.id}"
                    it.description.value = "Description ${it.id}"
                }
                .toList()
                .delay(1, TimeUnit.SECONDS)
                .doOnNext {
                    if (failForDebug) {
                        throw RuntimeException("Failed.")
                    }
                }
                .subscribe({
                    list.value += it
                    status.value = Status.COMPLETED
                }, {
                    status.value = Status.ERROR
                    error.value = it
                })
    }

    object Manager {
        val cache = Cache<Categories, String>()
        fun get(query: String): Categories {
            return cache.get(query) ?: Categories(query).apply { cache.put(this) }
        }

        fun add(category: Category): Observable<Category> {
            return Observable.just(category)
                    .delay(1, TimeUnit.SECONDS)
                    .doOnNext {
                        // Check duplicated id & name
                        cache.getAll().forEach {
                            if (category in it.list.value || it.list.value.find { it.name.value == category.name.value } != null) {
                                throw RuntimeException("$category is already exists.")
                            }
                            it.list.value += category // add
                        }
                        Category.Manager.cache.put(category)
                    }
        }
    }
}
