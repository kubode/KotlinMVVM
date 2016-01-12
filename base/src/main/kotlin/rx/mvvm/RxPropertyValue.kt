package rx.mvvm

import rx.subjects.BehaviorSubject

/**
 * 書き込み可能で、変更の通知を受けるタイプの[RxPropertyObservable]を生成する。
 *
 * 実体は[BehaviorSubject]になっているため、[Observable.subscribe]すると即`onNext`が呼ばれる。
 *
 * [rxProperty]と同時に定義すること。
 * でなければ、初期値が`null`になり、[NullPointerException]を引き起こす可能性がある。
 *
 * Usage:
 * ```
 * val intObservable = RxPropertyObservable<Int>()
 * var int by rxProperty(0, intObservable)
 * ```
 */
fun <T> RxPropertyObservable.Companion.value(defaultValue: T) = RxPropertyObservable(ValueState(defaultValue))

/**
 * 読み書き可能な[State]。
 */
private class ValueState<T>(defaultValue: T) : State<T>() {
    override val observable = BehaviorSubject.create<T>(defaultValue)
    override var value: T
        get() = observable.value
        set(value) = observable.onNext(value)
}
