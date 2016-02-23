package com.teamlab.kotlin.mvvm.ui.views

import android.support.v4.app.Fragment
import android.view.View
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

private fun <V, T> V.bindUi(observable: Observable<T>, onNext: V.(T) -> Unit): Subscription {
    return observable.observeOn(AndroidSchedulers.mainThread()).subscribe { onNext(it) }
}

fun <V : Fragment, T> V.bind(observable: Observable<T>, onNext: V.(T) -> Unit) = bindUi(observable, onNext)
fun <V : View, T> V.bind(observable: Observable<T>, onNext: V.(T) -> Unit) = bindUi(observable, onNext)
