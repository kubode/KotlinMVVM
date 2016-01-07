package com.teamlab.kotlin.mvvm.util

import android.content.Context
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Implements this interface if has [ObjectGraph].
 */
public interface HasObjectGraph {
    /**
     * Define [ObjectGraph].
     */
    val objectGraph: ObjectGraph
}

private interface Wrapper<V> {
    val value: V
}

private class NormalWrapper<V>(private val objectGraph: ObjectGraph,
                               private val initializer: (ObjectGraph) -> V) : Wrapper<V> {
    override val value: V
        get() = initializer(objectGraph)

    override fun toString(): String {
        return "$value"
    }
}

private class SingletonWrapper<V>(private val objectGraph: ObjectGraph,
                                  private val initializer: (ObjectGraph) -> V) : Wrapper<V> {
    override val value: V by lazy { initializer(objectGraph) }
    override fun toString(): String {
        return "$value(Singleton)"
    }
}

private enum class Type(private val wrapper: (ObjectGraph, (ObjectGraph) -> Any) -> Wrapper<*>) {
    NORMAL({ objectGraph, initializer -> NormalWrapper(objectGraph, initializer) }),
    SINGLETON({ objectGraph, initializer -> SingletonWrapper(objectGraph, initializer) });

    internal fun wrap(objectGraph: ObjectGraph, initializer: (ObjectGraph) -> Any): Wrapper<*> {
        return wrapper(objectGraph, initializer)
    }
}

private class Provider<V : Any>(internal val type: Type,
                                internal val initializer: (ObjectGraph) -> V)

/**
 * Define module by extend this class like below.
 *
 * ```
 * class MyModule : Module() {
 *     init {
 *         provide(Double::class, { Math.random() })
 *         provideSingleton(Int::class, { 1 }, "named")
 *         provideSingleton(String::class, { "Dependency: ${it.get(Int::class}" })
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
    protected fun <V : Any> provide(clazz: KClass<V>, initializer: (ObjectGraph) -> V, name: String? = null) {
        add(clazz, name, Provider(Type.NORMAL, initializer))
    }

    /**
     * Provide object as singleton.
     */
    protected fun <V : Any> provideSingleton(clazz: KClass<V>, initializer: (ObjectGraph) -> V, name: String? = null) {
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

/**
 * Find [ObjectGraph].
 * If this [Context] implements [HasObjectGraph] then returns `this.objectGraph`.
 * If applicationContext implements [HasObjectGraph] then returns `applicationContext.objectGraph`.
 * Otherwise throws exception.
 *
 * @return Founded [ObjectGraph].
 * @throws RuntimeException When [ObjectGraph] is not found.
 */
public fun Context.findObjectGraph(): ObjectGraph {
    if (this is HasObjectGraph) {
        return this.objectGraph
    }
    val application = this.applicationContext
    if (application is HasObjectGraph) {
        return application.objectGraph
    }
    throw  RuntimeException("${ObjectGraph::class.java.simpleName} is not found in $this.")
}

interface Injectable {
    val context: Context
}

fun <V : Any> Injectable.inject(clazz: KClass<V>, name: String? = null) = lazy { context.findObjectGraph().get(clazz, name) }

// Like kotlin lazy delegate but the initializer gets the target and metadata passed to it
internal class Lazy<T, V>(private val initializer: T.() -> V) : ReadOnlyProperty<T, V> {
    private object EMPTY

    @Suppress("UNCHECKED_CAST")
    private var value: V = EMPTY as V

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == EMPTY) {
            value = thisRef.initializer()
        }
        return value
    }
}
