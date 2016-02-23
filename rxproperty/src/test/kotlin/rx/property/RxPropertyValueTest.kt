package rx.property

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.Subscription

class RxPropertyValueTest {

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

    @Test fun initialized_by_default_value() {
        assertEquals(0, model.v)
    }

    @Test fun value_modified_on_subscribe() {
        assertEquals(0, value)
    }

    @Test fun value_modified_after_set_property() {
        model.v = 1
        assertEquals(1, value)
    }

    @Test fun value_not_modified_after_unsubscribe() {
        subscription.unsubscribe()
        model.v = 1
        assertEquals(0, value)
    }

    class MockModel {
        val o = RxPropertyObservable.value(0)
        var v by o.toProperty()
    }
}
