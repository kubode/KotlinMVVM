package com.teamlab.kotlin.mvvm.view

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.textChanges
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.butterknife.bindView
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.viewmodel.CategoryAddViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class CategoryAddDialogFragment : DialogFragment() {

    private val mainThread = AndroidSchedulers.mainThread()

    private val id: TextView by bindView(R.id.id)
    private val idValidation: TextView by bindView(R.id.id_validation)
    private val name: TextView by bindView(R.id.name)
    private val nameValidation: TextView by bindView(R.id.name_validation)
    private val description: TextView by bindView(R.id.description)
    private val descriptionValidation: TextView by bindView(R.id.description_validation)

    private val vm = CategoryAddViewModel()
    private lateinit var subscription: CompositeSubscription

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        return AlertDialog.Builder(activity)
                .setTitle("Add Category")
                .setView(R.layout.category_add)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create()
    }

    override fun onStart() {
        super.onStart()
        subscription = CompositeSubscription()
        // setup views
        val progress = ProgressDialog(activity).apply { isCancelable = false }
        id.text = vm.id.value
        name.text = vm.name.value
        description.text = vm.description.value
        val add = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

        // bind view model
        subscription.add(vm.status.observable
                .doOnUnsubscribe { progress.dismiss() }
                .observeOn(mainThread)
                .subscribe {
                    when (it!!) {
                        Status.NORMAL, Status.ERROR -> {
                            progress.dismiss()
                        }
                        Status.REQUESTING -> {
                            progress.show()
                        }
                        Status.COMPLETED -> {
                            progress.dismiss()
                            dismiss()
                        }
                    }
                })
        subscription.add(vm.error.observable
                .observeOn(mainThread)
                .subscribe {
                    it?.let {
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    }
                })
        subscription.add(vm.idValidation.observable
                .observeOn(mainThread)
                .subscribe {
                    idValidation.text = it
                })
        subscription.add(vm.nameValidation.observable
                .observeOn(mainThread)
                .subscribe {
                    nameValidation.text = it
                })
        subscription.add(vm.descriptionValidation.observable
                .observeOn(mainThread)
                .subscribe {
                    descriptionValidation.text = it
                })
        subscription.add(vm.addEnabled.observable
                .observeOn(mainThread)
                .subscribe {
                    add.isEnabled = it
                })

        // attach events
        subscription.add(id.textChanges()
                .subscribe {
                    vm.id.value = "$it"
                })
        subscription.add(name.textChanges()
                .subscribe {
                    vm.name.value = "$it"
                })
        subscription.add(description.textChanges()
                .subscribe {
                    vm.description.value = "$it"
                })
        subscription.add(add.clicks()
                .subscribe {
                    vm.add()
                })
    }

    override fun onStop() {
        subscription.unsubscribe()
        super.onStop()
    }
}
