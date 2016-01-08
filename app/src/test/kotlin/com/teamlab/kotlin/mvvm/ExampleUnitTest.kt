package com.teamlab.kotlin.mvvm

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {
    @Test
    fun case1() {
        val x = X()
        assertEquals(0, x.value)
        assertEquals(0, x.valueObservable.toBlocking().first())
        assertEquals(1, x.chainObservable.toBlocking().first())
        x.value = 2
        assertEquals(2, x.value)
        assertEquals(2, x.valueObservable.toBlocking().first())
        assertEquals(3, x.chainObservable.toBlocking().first())
    }

    @Test(expected = Exception::class)
    fun case2() {
        Y()
        fail()
    }

    @Test
    fun case3() {
        val x = X()
        var v = 0
        val s = x.chainObservable.subscribe { v = it }
        assertEquals(1, v)
        x.value = 1
        assertEquals(2, v)
        s.unsubscribe()
        x.value = 2
        assertEquals(2, v)
    }

    open class X {
        val valueObservable = RxPropertyObservable<Int>()
        val chainObservable = RxPropertyObservable(valueObservable.map { it + 1 })
        var value by rxProperty(0, valueObservable)
    }

    class Y : X() {
        var chain by rxProperty(0, chainObservable)
    }
}
