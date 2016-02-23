package rx.property

import rx.Observable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * [toProperty]を使い移譲プロパティを定義することで、変更通知を受けられる[Observable]。
 * 用途にそって[RxPropertyState]を実装し、コンストラクタの[state]へ渡すことで挙動を変更できる。
 *
 * [Companion]に拡張メソッドを定義することで拡張性を持たせる。
 *
 * Usage:
 * ```
 * val intObservable = RxPropertyObservable.value(0)
 * var int by intObservable.toProperty()
 *
 * intObservable.subscribe { log(it) } // logged: 0
 * int = 1 // logged: 1
 * ```
 */
class RxPropertyObservable<T>(private val state: RxPropertyState<T>) : Observable<T>(state) {

    companion object {}

    /**
     * この[RxPropertyObservable]をプロパティへ変換する。
     *
     * プロパティの変更通知は、この[RxPropertyObservable]へ通知される。
     * 読み書きがあるため、[state]が読み書きできない場合は例外が発生する。
     */
    fun toProperty() = object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return state.value
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            state.value = value
        }
    }
}
