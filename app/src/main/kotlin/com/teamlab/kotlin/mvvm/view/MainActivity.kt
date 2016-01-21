package com.teamlab.kotlin.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.event.AddAccountEvent
import com.teamlab.kotlin.mvvm.event.OpenUrlEvent
import com.teamlab.kotlin.mvvm.ext.of
import com.teamlab.kotlin.mvvm.util.RxEventBus
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.HasObjectGraphFinder
import com.teamlab.kotlin.mvvm.util.inject
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class MainActivity : AppCompatActivity(), Injectable {
    override val hasObjectGraphFinder = HasObjectGraphFinder.of(this)

    private val ref by inject(RefWatcher::class)
    private val bus by inject(RxEventBus::class)

    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        subscription = CompositeSubscription(
                bus.subscribe(AddAccountEvent::class, {
                    AddAccountDialogFragment().show(supportFragmentManager, null)
                }),
                bus.subscribe(OpenUrlEvent::class, {
                    startActivity(Intent.parseUri(it.url, 0))
                })
        )
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, AccountsFragment())
                    .commit()
        }
    }

    override fun onDestroy() {
        subscription.unsubscribe()
        super.onDestroy()
        ref.watch(this)
    }
}
