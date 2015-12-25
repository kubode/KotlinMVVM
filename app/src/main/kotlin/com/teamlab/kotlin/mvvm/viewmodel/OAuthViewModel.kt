package com.teamlab.kotlin.mvvm.viewmodel

import android.os.Bundle
import com.teamlab.kotlin.mvvm.RxProperty
import com.teamlab.kotlin.mvvm.ViewModel
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.model.Twitter

class OAuthViewModel(private val twitter:Twitter): ViewModel(){

    val status = RxProperty(Status.NORMAL)

    init {
        bind(status, twitter.oAuth.status.behaviorSubject)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
