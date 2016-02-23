package rx.property

import rx.Observable

/**
 * 他の[Observable]を連結し、通知を受ける。
 *
 * プロパティ化できない。
 */
fun <T> RxPropertyObservable.Companion.chain(observable: Observable<T>) = RxPropertyObservable(ChainState(observable))

/**
 * [Observable]を連結するための[RxPropertyState]。
 *
 * 値の読み書きは、例外が発生する。
 */
private class ChainState<T>(override val observable: Observable<T>) : RxPropertyState<T> {
    override var value: T
        get() = throw UnsupportedOperationException("Not support property access.")
        set(value) = throw UnsupportedOperationException("Not support property access.")
}
