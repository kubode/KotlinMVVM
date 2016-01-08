package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.ext.of
import com.teamlab.kotlin.mvvm.model.Account
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.InjectionHierarchy
import com.teamlab.kotlin.mvvm.util.bindView
import com.teamlab.kotlin.mvvm.util.inject
import com.teamlab.kotlin.mvvm.viewmodel.OAuthViewModel
import rx.mvvm.MvvmFragment

class OAuthFragment : MvvmFragment(), Injectable {
    override val injectionHierarchy = InjectionHierarchy.of(this)

    private val account: Account by inject(Account::class)

    private val progress: ProgressBar by bindView(R.id.progress)
    private val error: TextView by bindView(R.id.error)
    private val authGroup: View by bindView(R.id.auth_group)
    private val url: TextView by bindView(R.id.url)
    private val pin: EditText by bindView(R.id.pin)
    private val authenticate: Button by bindView(R.id.authenticate)

    override lateinit var vm: OAuthViewModel

    override fun onInitializeViewModel() {
        vm = OAuthViewModel(account)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.o_auth, container, false)
    }
}
