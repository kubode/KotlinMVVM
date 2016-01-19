package com.teamlab.kotlin.mvvm.util

import java.util.*
import kotlin.reflect.KClass

internal interface Provider<T> {
    fun get(objectGraph: ObjectGraph): T
}

internal class SingletonProvider<T>(private val initializer: ObjectGraph.() -> T) : Provider<T> {
    private object EMPTY

    @Suppress("UNCHECKED_CAST")
    private var value = EMPTY as T

    override fun get(objectGraph: ObjectGraph): T {
        if (value == EMPTY) {
            value = initializer(objectGraph)
        }
        return value
    }
}

internal class NormalProvider<T>(private val initializer: ObjectGraph.() -> T) : Provider<T> {
    override fun get(objectGraph: ObjectGraph): T {
        return initializer(objectGraph)
    }
}

/**
 * Define module by extend this class like below.
 *
 * ```
 * class MyModule : Module() {
 *     init {
 *         provide(Double::class, { Math.random() })
 *         provideSingleton(String::class, { "Dependency: ${get(Int::class}" })
 *     }
 * }
 * ```
 */
abstract class Module {
    internal val includes = LinkedList<Module>()
    internal val providers: MutableMap<KClass<*>, Provider<*>> = HashMap()

    internal fun <T : Any> add(clazz: KClass<T>, provider: Provider<*>) {
        providers.getOrPut(clazz, { provider })
    }

    /**
     * Include other [Module]
     */
    protected fun include(module: Module) {
        includes.add(module)
    }

    /**
     * Provide object.
     */
    protected fun <T : Any> provide(clazz: KClass<T>, initializer: ObjectGraph.() -> T) {
        add(clazz, NormalProvider(initializer))
    }

    /**
     * Provide object as singleton.
     */
    protected fun <T : Any> provideSingleton(clazz: KClass<T>, initializer: ObjectGraph.() -> T) {
        add(clazz, SingletonProvider(initializer))
    }
}

/**
 * Inheritable object graph.
 *
 * @constructor Creates object graph that inherited [parent].
 */
class ObjectGraph(private val parent: ObjectGraph? = null) {
    private val graph: MutableMap<KClass<*>, Provider<*>> = HashMap()

    /**
     * Add new [Module] to this.
     */
    fun add(module: Module): ObjectGraph {
        module.includes.forEach { add(it) }
        graph.putAll(module.providers)
        return this
    }

    /**
     * Get object from this.
     * If object not found then throw exception.
     *
     * @throws RuntimeException When object is not found.
     */
    public fun <T : Any> get(clazz: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return graph[clazz]?.get(this) as T?
                ?: throw RuntimeException("$clazz is not found.")
    }

    override fun toString(): String {
        return "{parent: $parent, graph: {$graph}}"
    }
}

/**
 * Implements this interface if has [ObjectGraph].
 */
interface HasObjectGraph {
    /**
     * Define [ObjectGraph].
     */
    val objectGraph: ObjectGraph
}

class HasObjectGraphFinder(internal vararg val finders: () -> Any?) {
    companion object {}
}

interface Injectable {
    val hasObjectGraphFinder: HasObjectGraphFinder
}

private fun Injectable.findObjectGraph(): ObjectGraph {
    hasObjectGraphFinder.finders.forEach {
        val obj = it() ?: return@forEach
        if (obj is HasObjectGraph) return obj.objectGraph
    }
    throw  RuntimeException("ObjectGraph is not found in $this.")
}

private val objectGraphCache = WeakHashMap<Injectable, ObjectGraph>()
val Injectable.parentObjectGraph: ObjectGraph
    get() = objectGraphCache.getOrPut(this, { findObjectGraph() })

fun <T : Any> Injectable.inject(clazz: KClass<T>) = lazy { parentObjectGraph.get(clazz) }
