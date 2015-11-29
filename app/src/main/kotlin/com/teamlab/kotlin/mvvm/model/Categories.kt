package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.Cache
import com.teamlab.kotlin.mvvm.Model
import com.teamlab.kotlin.mvvm.observable
import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import java.util.concurrent.TimeUnit

class Categories(query: String) : Model<String>() {

    override val id = query

    val listObservable = BehaviourSubject<List<Category>>()
    val statusObservable = BehaviourSubject<Status>()
    val errorObservable = BehaviourSubject<Throwable?>()

    var list by observable(emptyList(), listObservable)
    var status by observable(Status.NORMAL, statusObservable)
    var error by observable(null, errorObservable)

    fun requestIfNotCompleted() {
        if (status == Status.COMPLETED) {
            return
        }
        val failForDebug = status != Status.ERROR // DEBUG
        status = Status.REQUESTING
        error = null
        Observable.range(0, 5)
                .map { Category.Manager.get(it.toLong()) }
                .doOnNext {
                    it.name = "Category ${it.id}"
                    it.description = "Description ${it.id}"
                }
                .toList()
                .delay(1, TimeUnit.SECONDS)
                .doOnNext {
                    if (failForDebug) {
                        throw RuntimeException("Failed.")
                    }
                }
                .subscribe({
                    status = Status.COMPLETED
                    list += it
                }, {
                    status = Status.ERROR
                    error = it
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
                            if (category in it.list || it.list.find { it.name == category.name } != null) {
                                throw RuntimeException("$category is already exists.")
                            }
                            it.list += category // add
                        }
                        Category.Manager.cache.put(category)
                    }
        }
    }
}
