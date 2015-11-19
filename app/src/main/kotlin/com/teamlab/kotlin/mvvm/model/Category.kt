package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.MutableObservableProperty
import com.teamlab.kotlin.mvvm.ReadOnlyObservableProperty
import rx.Observable
import java.util.concurrent.TimeUnit

class Category(name: String, description: String) {
    val name = ReadOnlyObservableProperty(name)
    val description = MutableObservableProperty(description)
    val items = MutableObservableProperty(emptyList<Item>())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Category

        if (name.value != other.name.value) return false

        return true
    }

    override fun hashCode(): Int {
        return name.value.hashCode()
    }

    override fun toString(): String {
        return "name: ${name.value}, description: ${description.value}, items: ${items.value}"
    }

    object Manager {
        val categories = MutableObservableProperty(emptySet<Category>())

        fun add(category: Category): Observable<Category> {
            return Observable.just(category)
                    .delay(1, TimeUnit.SECONDS)
                    .doOnNext {
                        if (categories.value.contains(category)) {
                            throw RuntimeException("$category already exists.")
                        }
                        categories.value = categories.value + category
                    }
        }

        fun remove(category: Category): Observable<Category> {
            return Observable.just(category)
                    .delay(1, TimeUnit.SECONDS)
                    .doOnNext {
                        if (!categories.value.contains(category)) {
                            throw RuntimeException("$category is not exists.")
                        }
                        categories.value = categories.value - category
                    }
        }

        fun queryAll(): Observable<Set<Category>> {
            return Observable.just(Array(3, { Category("Name $it", "Description $it") }))
                    .map { it.toSet() }
                    .delay(1, TimeUnit.SECONDS)
                    .doOnNext { categories.value = it }
        }
    }
}
