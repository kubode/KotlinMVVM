package com.teamlab.kotlin.mvvm.util

/**
 * An event's abstract class.
 */
abstract class Event {
    internal var handledCount: Int = 0
}
