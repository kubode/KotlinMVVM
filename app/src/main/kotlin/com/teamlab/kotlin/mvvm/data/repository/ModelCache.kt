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
        @Suppress("UNCHECKED_CAST")
        return synchronized(cache) {
            val key = Pair(clazz.java, id)
            cache.get(key) as M? ?: creator(id).apply { cache.put(key, this) }
        }
    }
}
