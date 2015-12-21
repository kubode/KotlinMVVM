package com.teamlab.kotlin.mvvm

import rx.Observable
import rx.Subscription
import rx.subjects.BehaviorSubject

/**
 * 読み取り専用の[RxProperty]。
 *
 * @param T このプロパティの型。
 * @param initialValue このプロパティの初期値。
 */
open class ReadOnlyRxProperty<T>(initialValue: T) {
    private val behaviorSubjectInternal = BehaviorSubject.create(initialValue)
    open var value: T
        get() = behaviorSubjectInternal.value
        protected set(value) = behaviorSubjectInternal.onNext(value)
    /**
     * @see [rx.subjects.BehaviorSubject]
     */
    val behaviorSubject = behaviorSubjectInternal.asObservable()
    /**
     * @see [rx.subjects.PublishSubject]
     */
    val publishSubject = behaviorSubjectInternal.skip(1)

    /**
     * 他の[observable]をバインドし、その変更を受け取るようにする。
     *
     * @param observable 変更を受け取る[Observable]。他の[ReadOnlyRxProperty]から派生させる場合、[behaviorSubject]を連結すること。
     * @return [observable]を購読した[Subscription]。ライフサイクルに合わせ[Subscription.unsubscribe]すること。
     */
    fun <X : T> bind(observable: Observable<X>): Subscription {
        return observable.subscribe { value = it }
    }

    override fun toString(): String {
        return "$value"
    }
}

/**
 * [rx]を使ったリアクティブなプロパティ実装。
 *
 * [value]に書き込むことで、[behaviorSubject]と[publishSubject]に通知される。
 *
 * @see ReadOnlyRxProperty
 */
class RxProperty<T>(initialValue: T) : ReadOnlyRxProperty<T>(initialValue) {
    final override var value: T
        get() = super.value
        public set(value) {
            super.value = value
        }
}
