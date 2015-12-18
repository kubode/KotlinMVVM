package com.teamlab.kotlin.mvvm.view

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding.view.clicks
import com.teamlab.kotlin.mvvm.MvvmDialogFragment
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.bindEditText
import com.teamlab.kotlin.mvvm.bindRxProperty
import com.teamlab.kotlin.mvvm.butterknife.bindView
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.viewmodel.CategoryUpdateViewModel
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class CategoryUpdateDialogFragment : MvvmDialogFragment {

    private val KEY_ID = "id"

    override protected lateinit var vm: CategoryUpdateViewModel

    private val name: EditText by bindView(R.id.name)
    private val nameValidation: TextView by bindView(R.id.name_validation)
    private val description: EditText by bindView(R.id.description)
    private val descriptionValidation: TextView by bindView(R.id.description_validation)

    private lateinit var subscription: Subscription

    constructor() : super()

    constructor(id: Long) : super() {
        arguments = Bundle().apply {
            putLong(KEY_ID, id)
        }
    }

    override fun onCreateViewModel() {
        if (!arguments.containsKey(KEY_ID)) {
            throw RuntimeException("Argument $KEY_ID is not exists.")
        }
        val id = arguments.getLong(KEY_ID)
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
        // setup views
        val progress = ProgressDialog(activity).apply { isCancelable = false }
        val dialog = (dialog as AlertDialog)
        val update = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val delete = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

        subscription = CompositeSubscription(
                // bind view model
                progress.bindRxProperty(vm.status) { if (it == Status.REQUESTING) show() else dismiss() },
                bindRxProperty(vm.status) { if (it == Status.COMPLETED) dismiss() },
                bindRxProperty(vm.error) {
                    it?.let {
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    }
                },
                nameValidation.bindRxProperty(vm.nameValidation) { text = it },
                descriptionValidation.bindRxProperty(vm.descriptionValidation) { text = it },
                update.bindRxProperty(vm.updateEnabled) { isEnabled = it },
                // attach events
                vm.name.bindEditText(name),
                vm.description.bindEditText(description),
                update.clicks().subscribe { vm.update() },
                delete.clicks().subscribe { vm.delete() })
    }

    override fun onStop() {
        subscription.unsubscribe()
        super.onStop()
    }
}
