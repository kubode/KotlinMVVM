package rx.mvvm

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

private fun <V, T> V.bindUi(observable: RxPropertyObservable<T>,
                            onNext: V.(T) -> Unit): Subscription {
    return observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onNext(it) }
}

fun <V : Activity, T> V.bind(observable: RxPropertyObservable<T>, onNext: V.(T) -> Unit) = bindUi(observable, onNext)
fun <V : Fragment, T> V.bind(observable: RxPropertyObservable<T>, onNext: V.(T) -> Unit) = bindUi(observable, onNext)
fun <V : View, T> V.bind(observable: RxPropertyObservable<T>, onNext: V.(T) -> Unit) = bindUi(observable, onNext)
