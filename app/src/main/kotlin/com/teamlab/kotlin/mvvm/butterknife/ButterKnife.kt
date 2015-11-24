package com.teamlab.kotlin.mvvm.butterknife

import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.View
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public fun <V : View> Fragment.bindView(id: Int): ReadOnlyProperty<Fragment, V> {
    return FragmentViewProperty(id)
}

public fun <V : View> DialogFragment.bindView(id: Int): ReadOnlyProperty<DialogFragment, V> {
    return DialogFragmentViewProperty(id)
}

private val fragmentViewCache: WeakHashMap<View, MutableMap<Int, WeakReference<View>>> = WeakHashMap()
private val Fragment.viewCache: MutableMap<Int, WeakReference<View>>
    get() = view?.let {
        fragmentViewCache.getOrPut(it, { HashMap() })
    } ?: throw IllegalStateException("Fragment#getView() is null")

private class FragmentViewProperty<V : View>(private val id: Int) : ReadOnlyProperty<Fragment, V> {
    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        @Suppress("UNCHECKED_CAST")
        return thisRef.viewCache.getOrPut(id, { WeakReference(thisRef.view.findViewById(id)) })
                .get() as V? ?: throw NullPointerException("View ID $id for '${property.name}' not found.")
    }
}

private val dialogFragmentDialogCache: WeakHashMap<Dialog, MutableMap<Int, WeakReference<View>>> = WeakHashMap()
private val DialogFragment.dialogCache: MutableMap<Int, WeakReference<View>>
    get() = dialog?.let {
        dialogFragmentDialogCache.getOrPut(it, { HashMap() })
    } ?: throw IllegalStateException("DialogFragment#getDialog() is null")

private class DialogFragmentViewProperty<V : View>(private val id: Int) : ReadOnlyProperty<DialogFragment, V> {
    override fun getValue(thisRef: DialogFragment, property: KProperty<*>): V {
        @Suppress("UNCHECKED_CAST")
        return thisRef.dialogCache.getOrPut(id, { WeakReference(thisRef.dialog.findViewById(id)) })
                .get() as V? ?: throw NullPointerException("View ID $id for '${property.name}' not found.")
    }
}
