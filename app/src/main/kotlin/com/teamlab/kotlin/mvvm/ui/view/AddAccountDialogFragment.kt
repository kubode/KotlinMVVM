package com.teamlab.kotlin.mvvm.ui.view

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.github.kubode.rxeventbus.RxEventBus
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.textChanges
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.di.ApplicationComponent
import com.teamlab.kotlin.mvvm.event.OpenUrlEvent
import com.teamlab.kotlin.mvvm.ui.viewmodels.AddAccountViewModel
import com.teamlab.kotlin.mvvm.util.Toaster
import com.teamlab.kotlin.mvvm.util.bindView
import rx.Subscription
import rx.mvvm.bind
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class AddAccountDialogFragment : DialogFragment() {

    @Inject lateinit var ref: RefWatcher
    @Inject lateinit var bus: RxEventBus
    @Inject lateinit var vm: AddAccountViewModel

    private val progress by bindView<View>(R.id.progress)
    private val error by bindView<View>(R.id.error)
    private val message by bindView<TextView>(R.id.message)
    private val retry by bindView<View>(R.id.retry)
    private val auth by bindView<View>(R.id.auth)
    private val url by bindView<TextView>(R.id.url)
    private val pin by bindView<EditText>(R.id.pin)
    private val submit by bindView<View>(R.id.submit)

    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        ApplicationComponent.from(this).inject(this)
        super.onCreate(savedInstanceState)
        vm.performRestoreInstanceState(savedInstanceState)
        vm.getRequestTokenIfEnable()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setView(R.layout.add_account)
                .create()
    }

    override fun onStart() {
        super.onStart()
        subscription = CompositeSubscription(
                progress.bind(vm.isProgressVisibleObservable) { visibility = if (it) View.VISIBLE else View.GONE },
                error.bind(vm.isErrorVisibleObservable) { visibility = if (it) View.VISIBLE else View.GONE },
                message.bind(vm.messageObservable) { text = it },
                retry.clicks().subscribe { vm.getRequestTokenIfEnable() },
                auth.bind(vm.isAuthVisibleObservable) { visibility = if (it) View.VISIBLE else View.GONE },
                url.bind(vm.urlObservable) { text = it },
                url.clicks().subscribe { bus.post(OpenUrlEvent(url.text.toString())) },
                pin.textChanges().subscribe { vm.pin = it.toString() },
                submit.bind(vm.isSubmitEnableObservable) { isEnabled = it },
                submit.clicks().subscribe { vm.getAccessTokenIfEnable() },
                bind(vm.submitErrorMessageObservable) { if (!it.isNullOrEmpty()) Toaster.show(activity, it) },
                bind(vm.isCompletedObservable) { if (it) dismiss() }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vm.performSaveInstanceState(outState)
    }

    override fun onStop() {
        subscription.unsubscribe()
        super.onStop()
    }

    override fun onDestroy() {
        vm.performDestroy()
        super.onDestroy()
        ref.watch(this)
    }
}
