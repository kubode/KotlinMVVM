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
import com.teamlab.kotlin.mvvm.viewmodel.AddCategoryViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class AddCategoryDialogFragment : DialogFragment() {

    private val vm = AddCategoryViewModel()

    private val name: TextView by bindView(R.id.name)
    private val nameValidation: TextView by bindView(R.id.name_validation)
    private val description: TextView by bindView(R.id.description)
    private val descriptionValidation: TextView by bindView(R.id.description_validation)
    private lateinit var progress: ProgressDialog

    private lateinit var subscription: CompositeSubscription

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        return AlertDialog.Builder(activity)
                .setTitle("Add Category")
                .setView(R.layout.add_category)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", null)
                .create()
    }

    override fun onStart() {
        super.onStart()
        subscription = CompositeSubscription()

        // setup views
        progress = ProgressDialog(activity)
        val submit = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

        // bind view model
        subscription.add(vm.status.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            when (it!!) {
                AddCategoryViewModel.Status.NORMAL -> {
                    progress.dismiss()
                }
                AddCategoryViewModel.Status.POSTING -> {
                    progress.show()
                }
                AddCategoryViewModel.Status.ERROR -> {
                    progress.dismiss()
                }
                AddCategoryViewModel.Status.FINISHED -> {
                    progress.dismiss()
                    dismiss()
                }
            }
        })
        subscription.add(vm.error.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            it?.let {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        })
        subscription.add(vm.nameValidation.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            nameValidation.text = it
        })
        subscription.add(vm.descriptionValidation.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            descriptionValidation.text = it
        })
        subscription.add(vm.submitEnabled.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            submit.isEnabled = it
        })

        // attach events
        subscription.add(name.textChanges().subscribe {
            vm.name.value = "${name.text}"
        })
        subscription.add(description.textChanges().subscribe {
            vm.description.value = "${description.text}"
        })
        subscription.add(submit.clicks().subscribe {
            vm.post()
        })
    }

    override fun onStop() {
        subscription.unsubscribe()
        progress.dismiss()
        super.onStop()
    }
}
