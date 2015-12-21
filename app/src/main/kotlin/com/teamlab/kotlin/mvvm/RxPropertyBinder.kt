package com.teamlab.kotlin.mvvm

import android.support.annotation.MainThread
import android.widget.EditText
import com.jakewharton.rxbinding.widget.textChanges
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * [ReadOnlyRxProperty]を[V]にバインドする。
 *
 * メインスレッドにディスパッチされるので、[V]はUI以外指定しないこと。
 *
 * @param property
 * バインドする[ReadOnlyRxProperty]。
 * @param initializer
 * [V]を[ReadOnlyRxProperty.value]で初期化するコールバック。
 * [V]を`this`として扱う。
 * このメソッドをコール時に処理するので、このメソッド自体をメインスレッドから呼ぶ必要がある。
 * @param onNext
 * [ReadOnlyRxProperty.publishSubject]の通知時に呼ばれるコールバック。
 * [V]を`this`として扱う。
 * メインスレッドにディスパッチされてコールされる。
 */
@MainThread
fun <V, T> V.bindRxProperty(property: ReadOnlyRxProperty<T>,
                            initializer: V.(T) -> Unit,
                            onNext: V.(T) -> Unit): Subscription {
    val subscription = property.publishSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onNext(it) }
    initializer(property.value)
    return subscription
}

/**
 * @see bindRxProperty
 */
@MainThread
fun <V, T> V.bindRxProperty(property: ReadOnlyRxProperty<T>,
                            setter: V.(T) -> Unit) = bindRxProperty(property, setter, setter)

@MainThread
fun EditText.bindTextChanges(property: RxProperty<CharSequence>): Subscription {
    val subscription = textChanges().subscribe { property.value = it }
    setText(property.value)
    return subscription
}
