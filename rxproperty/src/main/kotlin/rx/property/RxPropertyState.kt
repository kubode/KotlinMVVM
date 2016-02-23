package rx.property

import rx.Observable
import rx.Subscriber

/**
 * [RxPropertyObservable]の状態管理クラス。
 */
abstract class RxPropertyState<T> : Observable.OnSubscribe<T> {

    /**
     * [Observable]としての移譲先となるプロパティ。
     * [rx.subjects.BehaviorSubject]の様に、Subscribe時に現在の値を返すようにする。
     */
    abstract val observable: Observable<T>
    /**
     * プロパティ化した際の移譲先となるプロパティ。
     * プロパティ化しない想定であれば、例外を投げても良い。
     */
    abstract var value: T

    final override fun call(child: Subscriber<in T>) {
        child.add(observable.subscribe { child.onNext(it) })
    }
}
