package com.teamlab.kotlin.mvvm.util

import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val fragmentCache = HashMap<Fragment, HashMap<Int, View>>()

class FragmentViewProperty<F : Fragment, R : Any, V : View>(private val root: F.() -> R,
                                                            private val find: R.(Int) -> View,
                                                            private val id: Int) : ReadOnlyProperty<F, V> {
    override fun getValue(thisRef: F, property: KProperty<*>): V {
        @Suppress("UNCHECKED_CAST")
        return fragmentCache.getOrPut(thisRef, { HashMap() })
                .getOrPut(id, { thisRef.root().find(id) }) as V
    }
}

fun <V : View> Fragment.bindView(id: Int)
        = FragmentViewProperty<Fragment, View, V>({ view!! }, { findViewById(it) }, id)

fun <V : View> DialogFragment.bindView(id: Int)
        = FragmentViewProperty<DialogFragment, Dialog, V>({ dialog }, { findViewById(it) }, id)

fun Fragment.unbindViews() {
    fragmentCache.remove(this)
}

@Suppress("UNCHECKED_CAST")
fun <V : View> RecyclerView.ViewHolder.bindView(id: Int) = lazy { itemView.findViewById(id) as V }
