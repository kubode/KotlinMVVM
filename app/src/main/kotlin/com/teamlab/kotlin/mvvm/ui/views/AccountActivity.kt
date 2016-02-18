package com.teamlab.kotlin.mvvm.ui.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.data.model.Account
import com.teamlab.kotlin.mvvm.data.repository.AccountRepository
import com.teamlab.kotlin.mvvm.di.ApplicationComponent

class AccountActivity : AppCompatActivity() {

    @javax.inject.Inject lateinit var accountRepository: AccountRepository
    lateinit var account: Account

    companion object {
        private const val EXTRA_ACCOUNT_ID = "accountId"
        fun createIntent(context: Context, accountId: Long) = Intent(context, AccountActivity::class.java)
                .putExtra(AccountActivity.EXTRA_ACCOUNT_ID, accountId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ApplicationComponent.from(this).inject(this)
        account = accountRepository.of(intent.getLongExtra(AccountActivity.EXTRA_ACCOUNT_ID, -1))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, TimelineFragment())
                    .commit()
        }
    }
}
