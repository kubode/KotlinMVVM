package com.teamlab.kotlin.mvvm.butterknife

import android.app.Activity
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.View
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val viewRootCache: WeakHashMap<Any, MutableMap<Int, WeakReference<View>>> = WeakHashMap()

private class ViewCacheProperty<T, R : Any?, V : View>(private val rootGetter: T.() -> R,
                                                       private val viewFinder: R.(Int) -> View?,
                                                       private val id: Int) : ReadOnlyProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        val root = thisRef.rootGetter()
                ?: throw NullPointerException("$thisRef's root is null.")
        @Suppress("UNCHECKED_CAST")
        return viewRootCache.getOrPut(root, { HashMap() })
                .getOrPut(id, {
                    WeakReference(root.viewFinder(id)
                            ?: throw NullPointerException("${property.name}(id: $id) is not found."))
                })
                .get() as V
    }
}

@Suppress("UNCHECKED_CAST")
fun <V : View> Activity.bindView(id: Int) = lazy { findViewById(id) as V }

@Suppress("UNCHECKED_CAST")
fun <V : View> View.bindView(id: Int) = lazy { findViewById(id) as V }

fun <V : View> Fragment.bindView(id: Int): ReadOnlyProperty<Fragment, V>
        = ViewCacheProperty<Fragment, View, V>({ view }, { findViewById(it) }, id)

fun <V : View> DialogFragment.bindView(id: Int): ReadOnlyProperty<DialogFragment, V>
        = ViewCacheProperty<Fragment, Dialog, V>({ dialog }, { findViewById(it) }, id)
