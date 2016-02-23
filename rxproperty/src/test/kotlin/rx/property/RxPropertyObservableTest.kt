package rx.property

import org.junit.After
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.Subscription
import rx.subjects.PublishSubject
import kotlin.test.assertEquals

class RxPropertyObservableTest {

    private lateinit var publish: PublishSubject<Int>
    private lateinit var state: MockState<Int>
    private lateinit var model: MockModel<Int>
    private var value: Int = -1
    private lateinit var subscription: Subscription

    @Before fun setUp() {
        publish = PublishSubject.create()
        state = MockState(publish, -1)
        model = MockModel(state)
        value = -1
        subscription = model.o.subscribe { value = it }
    }

    @After fun tearDown() {
        if (!subscription.isUnsubscribed) subscription.unsubscribe()
    }

    @Test fun value_modified() {
        publish.onNext(0)
        assertEquals(0, value)
    }

    @Test fun value_not_modified_after_unsubscribe() {
        subscription.unsubscribe()
        publish.onNext(0)
        assertEquals(-1, value)
    }

    @Test fun state_value_modified() {
        model.v = 0
        assertEquals(0, state.value)
    }

    class MockState<T>(override val observable: Observable<T>, override var value: T) : RxPropertyState<T>

    class MockModel<T>(state: RxPropertyState<T>) {
        val o = RxPropertyObservable(state)
        var v by o.toProperty()
    }
}
