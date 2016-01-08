package com.teamlab.kotlin.mvvm.ext

import android.content.Context
import android.support.v4.app.Fragment
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.InjectionHierarchy

fun InjectionHierarchy.Companion.of(context: Context): InjectionHierarchy {
    return InjectionHierarchy({ context }, { context.applicationContext })
}

fun <T> InjectionHierarchy.Companion.of(fragment: T): InjectionHierarchy where T : Fragment, T : Injectable {
    return InjectionHierarchy({ fragment.targetFragment }, { fragment.activity }, { fragment.activity?.application })
}
