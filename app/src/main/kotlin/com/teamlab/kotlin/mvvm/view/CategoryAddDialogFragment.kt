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
import com.teamlab.kotlin.mvvm.bindRxProperty
import com.teamlab.kotlin.mvvm.bindTextChanges
import com.teamlab.kotlin.mvvm.butterknife.bindView
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.viewmodel.CategoryAddViewModel
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class CategoryAddDialogFragment : MvvmDialogFragment() {

    private val id: EditText by bindView(R.id.id)
    private val idValidation: TextView by bindView(R.id.id_validation)
    private val name: EditText by bindView(R.id.name)
    private val nameValidation: TextView by bindView(R.id.name_validation)
    private val description: EditText by bindView(R.id.description)
    private val descriptionValidation: TextView by bindView(R.id.description_validation)

    override protected lateinit var vm: CategoryAddViewModel
    private lateinit var subscription: Subscription

    override fun onCreateViewModel() {
        vm = CategoryAddViewModel()
    }

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
        val add = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

        subscription = CompositeSubscription(
                // bind view model
                progress.bindRxProperty(vm.progressVisible) { if (it) show() else dismiss() },
                bindRxProperty(vm.status) { if (it == Status.COMPLETED) dismiss() },
                bindRxProperty(vm.error) {
                    it?.let {
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                    }
                },
                idValidation.bindRxProperty(vm.idValidation, { text = it }),
                nameValidation.bindRxProperty(vm.nameValidation, { text = it }),
                descriptionValidation.bindRxProperty(vm.descriptionValidation, { text = it }),
                add.bindRxProperty(vm.addEnabled, { isEnabled = it }),
                // attach events
                id.bindTextChanges(vm.id),
                name.bindTextChanges(vm.name),
                description.bindTextChanges(vm.description),
                add.clicks().subscribe { vm.add() })
    }

    override fun onStop() {
        subscription.unsubscribe()
        super.onStop()
    }
}
