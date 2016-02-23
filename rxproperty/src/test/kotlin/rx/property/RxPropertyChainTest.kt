package rx.property

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.Subscription
import rx.subjects.PublishSubject

class RxPropertyChainTest {

    private lateinit var model: MockModel
    private var value: Int = -1
    private lateinit var subscription: Subscription

    @Before fun setUp() {
        model = MockModel()
        value = -1
        subscription = model.o.subscribe { value = it }
    }

    @After fun tearDown() {
        if (!subscription.isUnsubscribed) subscription.unsubscribe()
    }

    @Test fun value_not_modified_before_onNext() {
        assertEquals(-1, value)
    }

    @Test fun value_modified_after_onNext() {
        model.publish.onNext(0)
        assertEquals(0, value)
    }

    @Test fun value_not_modified_after_unsubscribe() {
        subscription.unsubscribe()
        model.publish.onNext(0)
        assertEquals(-1, value)
    }

    @Test(expected = UnsupportedOperationException::class) fun error_on_property_get() {
        System.out.println(model.v)
    }

    @Test(expected = UnsupportedOperationException::class) fun error_on_property_set() {
        model.v = 0
    }

    class MockModel {
        val publish = PublishSubject.create<Int>()
        val o = RxPropertyObservable.chain(publish)
        var v by o.toProperty()
    }
}
