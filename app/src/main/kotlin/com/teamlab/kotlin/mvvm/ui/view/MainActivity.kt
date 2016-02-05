package com.teamlab.kotlin.mvvm.ui.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.kubode.rxeventbus.RxEventBus
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.di.ApplicationComponent
import com.teamlab.kotlin.mvvm.event.AccountClickEvent
import com.teamlab.kotlin.mvvm.event.AddAccountEvent
import com.teamlab.kotlin.mvvm.event.OpenUrlEvent
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var ref: RefWatcher
    @Inject lateinit var bus: RxEventBus

    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationComponent.from(this).inject(this)
        setContentView(R.layout.main)

        subscription = CompositeSubscription(
                bus.subscribe(AddAccountEvent::class.java, {
                    AddAccountDialogFragment().show(supportFragmentManager, null)
                }),
                bus.subscribe(OpenUrlEvent::class.java, {
                    startActivity(Intent.parseUri(it.url, 0))
                }),
                bus.subscribe(AccountClickEvent::class.java, {
                    startActivity(com.teamlab.kotlin.mvvm.ui.view.AccountActivity.createIntent(this, it.account.id))
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
