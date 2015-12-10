package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.itemClicks
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.butterknife.bindView
import com.teamlab.kotlin.mvvm.model.Category
import com.teamlab.kotlin.mvvm.model.Status
import com.teamlab.kotlin.mvvm.viewmodel.CategoriesViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class CategoriesFragment : Fragment() {

    private val QUERY = "All categories"

    private val vm = CategoriesViewModel(QUERY)
    private val mainThread = AndroidSchedulers.mainThread()

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
        val adapter = object : ArrayAdapter<Category>(activity, android.R.layout.simple_list_item_1) {
            override fun getItemId(position: Int): Long {
                return getItem(position).id
            }
        }
        listView.adapter = adapter
        toolbar.title = QUERY
        toolbar.inflateMenu(R.menu.categories)
        toolbar.setOnMenuItemClickListener {
            when (it?.itemId) {
                R.id.action_add -> {
                    CategoryAddDialogFragment()
                            .show(childFragmentManager, null)
                    true
                }
                else -> {
                    false
                }
            }
        }

        // bind view model
        subscription.add(vm.status.behaviorSubject
                .observeOn(mainThread)
                .subscribe {
                    when (it!!) {
                        Status.REQUESTING -> {
                            progress.visibility = View.VISIBLE
                            error.visibility = View.GONE
                            retry.visibility = View.GONE
                        }
                        Status.ERROR -> {
                            progress.visibility = View.GONE
                            error.visibility = View.VISIBLE
                            retry.visibility = View.VISIBLE
                        }
                        Status.NORMAL, Status.COMPLETED -> {
                            progress.visibility = View.GONE
                            error.visibility = View.GONE
                            retry.visibility = View.GONE
                        }
                    }
                })
        subscription.add(vm.error.behaviorSubject
                .observeOn(mainThread)
                .subscribe {
                    error.text = it?.message
                })
        subscription.add(vm.list.behaviorSubject
                .observeOn(mainThread)
                .subscribe {
                    adapter.clear()
                    adapter.addAll(it)
                })

        // attach events
        subscription.add(retry.clicks()
                .subscribe {
                    vm.reload()
                })
        subscription.add(listView.itemClicks()
                .subscribe {
                    CategoryUpdateDialogFragment(adapter.getItemId(it))
                            .show(childFragmentManager, null)
                })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription.unsubscribe()
    }
}
