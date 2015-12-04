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
import com.teamlab.kotlin.mvvm.viewmodel.CategoryUpdateViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class CategoryUpdateDialogFragment : DialogFragment {

    private val KEY_ID = "id"
    private val mainThread = AndroidSchedulers.mainThread()

    private val name: TextView by bindView(R.id.name)
    private val nameValidation: TextView by bindView(R.id.name_validation)
    private val description: TextView by bindView(R.id.description)
    private val descriptionValidation: TextView by bindView(R.id.description_validation)

    private lateinit var vm: CategoryUpdateViewModel
    private lateinit var subscription: CompositeSubscription

    constructor() : super()

    constructor(id: Long) : this() {
        arguments = Bundle().apply {
            putLong("id", id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!arguments.containsKey(KEY_ID)) {
            throw RuntimeException("Argument $KEY_ID is not exists.")
        }
        val id = arguments.getLong("id")
        vm = CategoryUpdateViewModel(id)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        return AlertDialog.Builder(activity)
                .setTitle("Update Category")
                .setView(R.layout.category_update)
                .setPositiveButton("Update", null)
                .setNeutralButton("Delete", null)
                .setNegativeButton("Cancel", null)
                .create()
    }

    override fun onStart() {
        super.onStart()
        subscription = CompositeSubscription()
        // setup views
        val progress = ProgressDialog(activity).apply { isCancelable = false }
        name.text = vm.name.value
        description.text = vm.description.value
        val dialog = (dialog as AlertDialog)
        val update = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val delete = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

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
        subscription.add(vm.updateEnabled.observable
                .observeOn(mainThread)
                .subscribe {
                    update.isEnabled = it
                })

        // attach events
        subscription.add(name.textChanges()
                .subscribe {
                    vm.name.value = "$it"
                })
        subscription.add(description.textChanges()
                .subscribe {
                    vm.description.value = "$it"
                })
        subscription.add(update.clicks()
                .subscribe {
                    vm.update()
                })
        subscription.add(delete.clicks()
                .subscribe {
                    vm.delete()
                })
    }

    override fun onStop() {
        subscription.unsubscribe()
        super.onStop()
    }
}
