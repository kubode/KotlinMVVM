package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jakewharton.rxbinding.view.clicks
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.butterknife.bindView
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.viewmodel.CategoriesViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class CategoriesFragment : Fragment() {

    private val vm = CategoriesViewModel()

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val listView: ListView by bindView(R.id.list_view)
    private val progress: ProgressBar by bindView(R.id.progress)
    private val error: TextView by bindView(R.id.error)
    private val retry: Button by bindView(R.id.retry)

    private lateinit var subscription: CompositeSubscription

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.categories, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscription = CompositeSubscription()

        // setup views
        val adapter = ArrayAdapter<Category>(activity, android.R.layout.simple_list_item_1)
        listView.adapter = adapter
        toolbar.inflateMenu(R.menu.categories)
        toolbar.setOnMenuItemClickListener {
            when (it!!.itemId) {
                R.id.action_add -> {
                    AddCategoryDialogFragment().show(childFragmentManager, null)
                    true
                }
                else -> {
                    false
                }
            }
        }

        // bind view model
        subscription.add(vm.status.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            when (it!!) {
                CategoriesViewModel.Status.LOADING -> {
                    progress.visibility = View.VISIBLE
                    error.visibility = View.GONE
                    retry.visibility = View.GONE
                }
                CategoriesViewModel.Status.ERROR -> {
                    progress.visibility = View.GONE
                    error.visibility = View.VISIBLE
                    retry.visibility = View.VISIBLE
                }
                CategoriesViewModel.Status.INITIALIZED -> {
                    progress.visibility = View.GONE
                    error.visibility = View.GONE
                    retry.visibility = View.GONE
                }
            }
        })
        subscription.add(vm.error.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            error.text = it?.message
        })
        subscription.add(vm.categories.observable.observeOn(AndroidSchedulers.mainThread()).subscribe {
            adapter.clear()
            adapter.addAll(it)
        })

        // attach events
        subscription.add(retry.clicks().subscribe {
            vm.loadIfNotInitialized()
        })

        // do command
        vm.loadIfNotInitialized()
    }

    override fun onDestroyView() {
        subscription.unsubscribe()
        super.onDestroyView()
    }
}
