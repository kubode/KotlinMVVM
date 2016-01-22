package rx.mvvm

import rx.Observable
import rx.Subscriber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * [toProperty]を経由することで、プロパティの変更通知を受けられる[Observable]。
 * [subscribe]することで、プロパティの変更通知を受けられる。
 *
 * 用途にそって[State]を実装し、[Companion]に拡張メソッドを定義することで拡張性を持たせる。
 *
 * Usage:
 * ```
 * val intObservable = RxPropertyObservable.value(0)
 * var int by intObservable.asProperty()
 *
 * intObservable.subscribe { log(it) } // logged: 0
 * int = 1 // logged: 1
 * ```
 */
class RxPropertyObservable<T> : Observable<T> {

    companion object {}

    internal val state: State<T>

    constructor(state: State<T>) : super(state) {
        this.state = state
    }

    /**
     * この[RxPropertyObservable]をプロパティへ変換する。
     *
     * プロパティの変更通知は、この[RxPropertyObservable]へ通知される。
     * 読み書きがあるため、[state]が読み書きできない場合は例外が発生する。
     */
    fun toProperty(): ReadWriteProperty<Any, T> {
        return RxProperty(this)
    }
}

/**
 * [RxPropertyObservable.toProperty]の実体。
 *
 * 読み書きは[state]に対して行う。
 */
internal class RxProperty<R, T> : ReadWriteProperty<R, T> {
    private val state: State<T>

    internal constructor(observable: RxPropertyObservable<T>) {
        this.state = observable.state
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
