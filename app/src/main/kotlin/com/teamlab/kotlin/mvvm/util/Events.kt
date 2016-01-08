package com.teamlab.kotlin.mvvm.util

import rx.Scheduler
import rx.Subscription
import rx.subjects.PublishSubject
import kotlin.reflect.KClass

/**
 * [rx]を使ったイベントバス
 */
class EventBus {
    private val publishSubject: PublishSubject<Event> = PublishSubject.create()

    /**
     * イベントを投げる。ハンドリングされなかった場合は新たに[DeadEvent]を投げる。
     */
    fun post(event: Event) {
        publishSubject.onNext(event)
        if (event.handledCount == 0) {
            publishSubject.onNext(DeadEvent(event))
        }
    }

    /**
     * イベントのハンドリング登録。
     * 必ず戻り値に対して[Subscription.unsubscribe]をすること。
     */
    fun <E : Event> subscribe(clazz: KClass<E>, onNext: E.() -> Unit, scheduler: Scheduler? = null): Subscription {
        return publishSubject
                .ofType(clazz.java)
                .doOnNext { it.handledCount++ }
                .run { scheduler?.let { this.observeOn(it) } ?: this }
                .subscribe(onNext)
    }
}

/**
 * イベントの抽象クラス
 */
abstract class Event {
    internal var handledCount: Int = 0
}

/**
 * [EventBus.post]されたイベントがハンドリングされなかった場合、このイベントでラップして再度投げる
 */
class DeadEvent(val event: Event) : Event()
