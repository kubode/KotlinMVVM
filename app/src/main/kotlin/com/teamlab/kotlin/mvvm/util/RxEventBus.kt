package com.teamlab.kotlin.mvvm.util

import rx.Scheduler
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import kotlin.reflect.KClass

/**
 * An implementation of event bus using [PublishSubject].
 *
 *
 * Other specifications:
 * * MT-Safe.
 * * Not support generics.
 *
 * Note:
 * * You can use this as a singleton by defining `object RxEventBusSingleton: RxEventBus()`.
 */
open class RxEventBus {
    private val publishSubject = PublishSubject.create<Event>().toSerialized()

    /**
     * Posts an [Event] to subscribed handlers.
     *
     * If no handlers have been subscribed for [event]'s class, [unhandled] will be called with unhandled [event].
     *
     * @param event An [Event] to post.
     * @param unhandled Will be called if [event] is not handled.
     * Note: If handler subscribed by using async [Scheduler], it can't guarantee event is actually handled.
     */
    fun <E : Event> post(event: E, unhandled: (E) -> Unit = {}) {
        publishSubject.onNext(event)
        if (event.handledCount == 0) {
            unhandled(event)
        }
    }

    /**
     * Subscribes [handler] to receive events typed of [clazz].
     *
     * You should call [Subscription.unsubscribe] if you want to stop receiving events.
     *
     * @param clazz Type of event that you want to receive.
     * @param handler An event handler function that called if an event is posted.
     * @param scheduler [handler] will dispatched on this.
     */
    fun <E : Event> subscribe(clazz: KClass<E>, handler: (event: E) -> Unit, scheduler: Scheduler = Schedulers.immediate()): Subscription {
        return publishSubject
                .ofType(clazz.java)
                .doOnNext { it.handledCount++ }
                .observeOn(scheduler)
                .subscribe(handler)
    }
}
