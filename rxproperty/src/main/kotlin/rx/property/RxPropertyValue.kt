package rx.property

import rx.subjects.BehaviorSubject

/**
 * 値の読み書き可能な[RxPropertyObservable]を生成する。
 *
 * Usage:
 * ```
 * val intObservable = RxPropertyObservable.value(1)
 * var int by intObservable.toProperty()
 * ```
 */
fun <T> RxPropertyObservable.Companion.value(defaultValue: T) = RxPropertyObservable(ValueState(defaultValue))

/**
 * 読み書き可能な[RxPropertyState]。
 */
private class ValueState<T>(defaultValue: T) : RxPropertyState<T> {
    override val observable = BehaviorSubject.create(defaultValue)
    override var value: T
        get() = observable.value
        set(value) = synchronized(observable) { observable.onNext(value) }
}
