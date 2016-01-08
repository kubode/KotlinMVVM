package com.teamlab.kotlin.mvvm.viewmodel

import android.content.Context
import com.teamlab.kotlin.mvvm.ext.TwitterFactory
import com.teamlab.kotlin.mvvm.model.OAuth
import com.teamlab.kotlin.mvvm.model.State
import rx.Observable
import rx.mvvm.*

/**
 * Created by mkubode on 2016/01/08.
 */
class AddAccountViewModel(context: Context) : ViewModel() {

    private val oAuth = OAuth(context, TwitterFactory.create())

    val isProgressVisibleObservable = RxPropertyObservable.chain(Observable.combineLatest(
            oAuth.stateObservable, oAuth.requestTokenObservable,
            { state, requestToken -> state == State.REQUESTING && requestToken == null }
    ))
    val isErrorVisibleObservable = RxPropertyObservable.chain(Observable.combineLatest(
            oAuth.stateObservable, oAuth.requestTokenObservable,
            { state, requestToken -> state == State.ERROR && requestToken == null }
    ))
    val messageObservable = RxPropertyObservable.chain(oAuth.errorObservable.map { it?.message })
    val isAuthVisibleObservable = RxPropertyObservable.chain(oAuth.requestTokenObservable.map { it != null })
    val urlObservable = RxPropertyObservable.chain(oAuth.requestTokenObservable.map { it?.authorizationURL })
    val pinObservable = RxPropertyObservable.value<String>()
    var pin by rxProperty("", pinObservable)
    val isSubmitEnableObservable = RxPropertyObservable.chain(Observable.combineLatest(
            oAuth.stateObservable, pinObservable,
            { state, pin -> state != State.REQUESTING && pin.isNotEmpty() }))
    val submitErrorMessageObservable = RxPropertyObservable.chain(Observable.combineLatest(
            oAuth.requestTokenObservable, oAuth.errorObservable,
            { requestToken, error -> if (requestToken != null) error?.message else null }
    ))
    val isCompletedObservable = RxPropertyObservable.chain(oAuth.stateObservable.map { it == State.COMPLETED })

    fun getRequestTokenIfEnable() = oAuth.getRequestTokenIfEnable()
    fun getAccessTokenIfEnable() = oAuth.getAccessTokenIfEnable(pin)

    init {
        getRequestTokenIfEnable()
    }
}
