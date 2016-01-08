package rx.mvvm

import rx.Observable
import rx.Subscriber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * このプロパティの変更通知を[observable]で受ける。
 *
 * Usage:
 * ```
 * val intObservable = RxPropertyObservable<Int>()
 * var int by rxProperty(0, intObservable)
 *
 * intObservable.subscribe { log(it) } // logged: 0
 * int = 1 // logged: 1
 * ```
 *
 * @param initialValue 初期値。
 * @param observable プロパティの変更通知を受ける[RxPropertyObservable]。
 * バインド時に[initialValue]で初期化される。
 * 書き込みがあるため、他の[RxPropertyObservable]を連結させている場合は例外が発生する。
 */
fun <T> rxProperty(initialValue: T, observable: RxPropertyObservable<T>): ReadWriteProperty<Any, T> {
    return RxProperty(initialValue, observable)
}

/**
 * [rxProperty]と併用するための[Observable]。
 * [subscribe]することで、プロパティの変更通知を受けられる。
 *
 * 用途にそって[State]を実装し、[Companion]に拡張メソッドを定義することで拡張性を持たせる。
 */
class RxPropertyObservable<T> : Observable<T> {

    companion object {}

    internal val state: State<T>

    constructor(state: State<T>) : super(state) {
        this.state = state
    }
}

/**
 * [rxProperty]の実体。
 *
 * 読み書きは[state]に対して行う。
 */
internal class RxProperty<R, T> : ReadWriteProperty<R, T> {
    private val state: State<T>

    internal constructor(initialValue: T, observable: RxPropertyObservable<T>) {
        this.state = observable.state
        this.state.value = initialValue
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        return state.value
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        state.value = value
    }
}

/**
 * [RxPropertyObservable]の状態管理クラス。
 */
abstract class State<T> : Observable.OnSubscribe<T> {
    internal abstract val observable: Observable<T>
    internal abstract var value: T
    final override fun call(child: Subscriber<in T>) {
        child.add(observable.subscribe { child.onNext(it) })
    }
}
