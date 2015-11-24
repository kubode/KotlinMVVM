package com.teamlab.kotlin.mvvm.view

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.textChanges
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.butterknife.bindView
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.viewmodel.CategoryEditViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class CategoryEditDialogFragment : DialogFragment {

    private val KEY_ID = "id"
    private val mainThread = AndroidSchedulers.mainThread()

    private val name: TextView by bindView(R.id.name)
    private val nameValidation: TextView by bindView(R.id.name_validation)
    private val description: TextView by bindView(R.id.description)
    private val descriptionValidation: TextView by bindView(R.id.description_validation)

    private lateinit var vm: CategoryEditViewModel
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
        vm = CategoryEditViewModel(id)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        Log.v("X", "onCreateDialog")
        return AlertDialog.Builder(activity)
                .setTitle("Add Category")
                .setView(R.layout.category_edit)
                .setPositiveButton("Update", null)
                .setNeutralButton("Delete", null)
                .setNegativeButton("Cancel", null)
                .create()
    }

    override fun onResume() {
        Log.v("X", "onResume")
        super.onResume()
    }

    override fun onStart() {
        Log.v("X", "onStart")
        super.onStart()
        subscription = CompositeSubscription()
        // setup views
        val progress = ProgressDialog(activity)
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
        subscription.add(vm.name.observable
                .observeOn(mainThread)
                .subscribe {
                    name.text = it
                })
        subscription.add(vm.nameValidation.observable
                .observeOn(mainThread)
                .subscribe {
                    nameValidation.text = it
                })
        subscription.add(vm.description.observable
                .observeOn(mainThread)
                .subscribe {
                    description.text = it
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
                .skip(1) // 循環参照防ぐ
                .subscribe {
                    vm.name.value = "$it"
                })
        subscription.add(description.textChanges()
                .skip(1) // 循環参照防ぐ
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
