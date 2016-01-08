package rx.mvvm

import rx.Observable

/**
 * 他の[Observable]を連結し、通知を受ける。
 *
 * [rxProperty]に使用してはならない。使用した場合、例外が発生する。
 */
fun <T> RxPropertyObservable.Companion.chain(observable: Observable<T>) = RxPropertyObservable(ChainState(observable))

/**
 * [Observable]を連結するための[State]。
 *
 * 値の読み書きは、例外が発生する。
 */
private class ChainState<T>(observable: Observable<T>) : State<T>() {
    override val observable = observable
    override var value: T
        get() = throw UnsupportedOperationException("Can't get value because it's chained.")
        set(value) = throw UnsupportedOperationException("Can't set value because it's chained.")
}
