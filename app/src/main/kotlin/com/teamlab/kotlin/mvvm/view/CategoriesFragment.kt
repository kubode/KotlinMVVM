package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jakewharton.rxbinding.support.v7.widget.itemClicks
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.itemClicks
import com.teamlab.kotlin.mvvm.MvvmFragment
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.bindRxProperty
import com.teamlab.kotlin.mvvm.butterknife.bindView
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.viewmodel.CategoriesViewModel
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class CategoriesFragment : MvvmFragment() {

    private val QUERY = "All categories"

    override protected lateinit var vm: CategoriesViewModel

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val listView: ListView by bindView(R.id.list_view)
    private val progress: ProgressBar by bindView(R.id.progress)
    private val error: TextView by bindView(R.id.error)
    private val retry: Button by bindView(R.id.retry)

    private lateinit var subscription: Subscription

    override fun onCreateViewModel() {
        vm = CategoriesViewModel(QUERY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.categories, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // setup views
        val adapter = object : ArrayAdapter<Category>(activity, android.R.layout.simple_list_item_1) {
            override fun getItemId(position: Int): Long {
                return getItem(position).id
            }
        }
        listView.adapter = adapter
        toolbar.title = QUERY
        toolbar.inflateMenu(R.menu.categories)

        subscription = CompositeSubscription(
                // bind view model
                progress.bindRxProperty(vm.progressVisible) { visibility = it.toVisibility() },
                error.bindRxProperty(vm.errorVisible) { visibility = it.toVisibility() },
                retry.bindRxProperty(vm.errorVisible) { visibility = it.toVisibility() },
                adapter.bindRxProperty(vm.list) {
                    clear()
                    addAll(it)
                },
                // attach events
                toolbar.itemClicks().subscribe {
                    when (it.itemId) {
                        R.id.action_add -> {
                            CategoryAddDialogFragment()
                                    .show(childFragmentManager, null)
                        }
                    }
                },
                retry.clicks().subscribe { vm.reload() },
                listView.itemClicks().subscribe {
                    CategoryUpdateDialogFragment(adapter.getItemId(it))
                            .show(childFragmentManager, null)
                })
    }

    override fun onDestroyView() {
        subscription.unsubscribe()
        super.onDestroyView()
    }

    private fun Boolean.toVisibility() = if (this) View.VISIBLE else View.GONE
}
