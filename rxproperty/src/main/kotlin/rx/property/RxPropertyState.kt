package rx.property

import rx.Observable

/**
 * [RxPropertyObservable]の状態管理用インターフェース。
 */
interface RxPropertyState<T> {

    /**
     * [Observable]としての移譲先となるプロパティ。
     * [rx.subjects.BehaviorSubject]の様に、Subscribe時に現在の値を返すようにする。
     */
    val observable: Observable<T>
    /**
     * プロパティ化した際の移譲先となるプロパティ。
     * プロパティ化しない想定であれば、例外を投げても良い。
     */
    var value: T
}
