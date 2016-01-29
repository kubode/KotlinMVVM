package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v4.app.Fragment
import com.teamlab.kotlin.mvvm.di.AccountComponent
import com.teamlab.kotlin.mvvm.viewmodel.TimelineViewModel
import javax.inject.Inject

class TimelineFragment : Fragment() {

    @Inject lateinit var vm: TimelineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AccountComponent.from(activity).inject(this)
    }
}
