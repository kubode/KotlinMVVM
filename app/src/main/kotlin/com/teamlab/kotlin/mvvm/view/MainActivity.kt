package com.teamlab.kotlin.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.teamlab.kotlin.mvvm.MyApplication
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.event.AddAccountEvent
import com.teamlab.kotlin.mvvm.event.OpenUrlEvent
import com.teamlab.kotlin.mvvm.ext.of
import com.teamlab.kotlin.mvvm.util.EventBus
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.InjectionHierarchy
import com.teamlab.kotlin.mvvm.util.inject
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class MainActivity : AppCompatActivity(), Injectable {
    override val injectionHierarchy = InjectionHierarchy.of(this)

    private val bus by inject(EventBus::class)

    private lateinit var subscription: Subscription
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        subscription = CompositeSubscription(
                bus.subscribe(AddAccountEvent::class, {
                    AddAccountDialogFragment().show(supportFragmentManager, null)
                }),
                bus.subscribe(OpenUrlEvent::class, {
                    startActivity(Intent.parseUri(url, 0))
                })
        )
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, AccountsFragment())
                    .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as MyApplication).ref.watch(this)
    }
}
