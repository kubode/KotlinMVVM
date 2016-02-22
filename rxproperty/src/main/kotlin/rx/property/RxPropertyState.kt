package rx.property

import rx.Observable
import rx.Subscriber

/**
 * [RxPropertyObservable]の状態管理クラス。
 */
abstract class RxPropertyState<T> : Observable.OnSubscribe<T> {

    abstract val observable: Observable<T>
    abstract var value: T

    final override fun call(child: Subscriber<in T>) {
        child.add(observable.subscribe { child.onNext(it) })
    }
}
