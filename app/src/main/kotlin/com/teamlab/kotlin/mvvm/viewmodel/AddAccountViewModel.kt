package com.teamlab.kotlin.mvvm.viewmodel

import android.os.Bundle
import com.teamlab.kotlin.mvvm.model.OAuth
import com.teamlab.kotlin.mvvm.model.State
import rx.Observable
import rx.mvvm.RxPropertyObservable
import rx.mvvm.ViewModel
import rx.mvvm.chain
import rx.mvvm.value
import javax.inject.Inject

class AddAccountViewModel @Inject constructor(private val oAuth: OAuth) : ViewModel() {

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
    val pinObservable = RxPropertyObservable.value("")
    var pin by pinObservable.toProperty()
    val isSubmitEnableObservable = RxPropertyObservable.chain(Observable.combineLatest(
            oAuth.stateObservable, pinObservable,
            { state, pin -> state != State.REQUESTING && pin.isNotEmpty() }))
    val submitErrorMessageObservable = RxPropertyObservable.chain(Observable.combineLatest(
            oAuth.requestTokenObservable, oAuth.errorObservable,
            { requestToken, error -> if (requestToken != null) error?.message else null }
    ))
    val isCompletedObservable = RxPropertyObservable.chain(oAuth.stateObservable.map { it == State.COMPLETED })

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        oAuth.restore(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        oAuth.save(outState)
    }

    fun getRequestTokenIfEnable() = oAuth.getRequestTokenIfEnable()
    fun getAccessTokenIfEnable() = oAuth.getAccessTokenIfEnable(pin)
}
