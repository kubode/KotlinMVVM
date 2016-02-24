package com.teamlab.kotlin.mvvm.data.repository

import android.util.LruCache
import com.teamlab.kotlin.mvvm.di.ApplicationScope
import rx.mvvm.Model
import javax.inject.Inject
import kotlin.reflect.KClass

@ApplicationScope
class ModelCache @Inject constructor() {

    private val cache = LruCache<Pair<Class<*>, *>, Model<*>>(512)

    fun <I : Any, M : Model<I>> getOrPut(clazz: KClass<M>, id: I, creator: (id: I) -> M): M {
        val key = Pair(clazz.java, id)
        @Suppress("UNCHECKED_CAST")
        return synchronized(cache) {
            cache.get(key) as M? ?: creator(id).apply { cache.put(key, this) }
        }
    }

    fun <I : Any, M : Model<I>> touch(model: M) {
        val key = Pair(model.javaClass, model.id)
        synchronized(cache) {
            cache.get(key) ?: cache.put(key, model)
        }
    }
}
