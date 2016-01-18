package com.teamlab.kotlin.mvvm.util

import java.util.*
import kotlin.reflect.KClass

/**
 * Implements this interface if has [ObjectGraph].
 */
interface HasObjectGraph {
    /**
     * Define [ObjectGraph].
     */
    val objectGraph: ObjectGraph
}

private interface Wrapper<V> {
    val value: V
}

private class NormalWrapper<V>(private val objectGraph: ObjectGraph,
                               private val initializer: ObjectGraph.() -> V) : Wrapper<V> {
    override val value: V
        get() = initializer(objectGraph)

    override fun toString(): String {
        return "$value"
    }
}

private class SingletonWrapper<V>(private val objectGraph: ObjectGraph,
                                  private val initializer: ObjectGraph.() -> V) : Wrapper<V> {
    override val value: V by lazy { initializer(objectGraph) }
    override fun toString(): String {
        return "$value(Singleton)"
    }
}

private enum class Type(private val wrapper: (ObjectGraph, ObjectGraph.() -> Any) -> Wrapper<*>) {
    NORMAL({ objectGraph, initializer -> NormalWrapper(objectGraph, initializer) }),
    SINGLETON({ objectGraph, initializer -> SingletonWrapper(objectGraph, initializer) });

    internal fun wrap(objectGraph: ObjectGraph, initializer: ObjectGraph.() -> Any): Wrapper<*> {
        return wrapper(objectGraph, initializer)
    }
}

private class Provider<V : Any>(internal val type: Type,
                                internal val initializer: ObjectGraph.() -> V)

/**
 * Define module by extend this class like below.
 *
 * ```
 * class MyModule : Module() {
 *     init {
 *         provide(Double::class, { Math.random() })
 *         provideSingleton(Int::class, { 1 }, "named")
 *         provideSingleton(String::class, { "Dependency: ${get(Int::class}" })
 *     }
 * }
 * ```
 */
public abstract class Module {
    internal val includes: MutableList<Module> = LinkedList()
    @Suppress("EXPOSED_PROPERTY_TYPE")
    internal val providers: MutableMap<Class<*>, MutableMap<String?, Provider<*>>> = HashMap()

    private fun <V : Any> add(clazz: KClass<V>, name: String? = null, provider: Provider<*>) {
        providers.getOrPut(clazz.java, { HashMap() }).put(name, provider)
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
    protected fun <V : Any> provide(clazz: KClass<V>, initializer: ObjectGraph.() -> V, name: String? = null) {
        add(clazz, name, Provider(Type.NORMAL, initializer))
    }

    /**
     * Provide object as singleton.
     */
    protected fun <V : Any> provideSingleton(clazz: KClass<V>, initializer: ObjectGraph.() -> V, name: String? = null) {
        add(clazz, name, Provider(Type.SINGLETON, initializer))
    }
}

/**
 * Inheritable object graph.
 *
 * @constructor Creates object graph that inherited [parent].
 */
public class ObjectGraph(private val parent: ObjectGraph? = null) {
    private val graph: MutableMap<Class<*>, MutableMap<String?, Wrapper<*>>> = HashMap()

    /**
     * Add new [Module] to this.
     */
    public fun add(module: Module): ObjectGraph {
        module.includes.forEach { add(it) }
        module.providers.forEach {
            graph.getOrPut(it.key, { HashMap() }).putAll(it.value.mapValues {
                it.value.type.wrap(this, it.value.initializer)
            })
        }
        return this
    }

    /**
     * Get object from this.
     * If object not found then throw exception.
     *
     * @throws RuntimeException When object is not found.
     */
    public fun <V : Any> get(clazz: KClass<V>, name: String? = null): V {
        @Suppress("UNCHECKED_CAST")
        return graph[clazz.java]?.get(name)?.value as V?
                ?: parent?.get(clazz, name)
                ?: throw RuntimeException("${clazz.java}(${name ?: "No name"}) is not found.")
    }

    override fun toString(): String {
        return "{parent: $parent, graph: {$graph}}"
    }
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

fun <V : Any> Injectable.inject(clazz: KClass<V>, name: String? = null) = lazy { parentObjectGraph.get(clazz, name) }
