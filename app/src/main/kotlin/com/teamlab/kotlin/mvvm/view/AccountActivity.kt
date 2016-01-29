package com.teamlab.kotlin.mvvm.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.teamlab.kotlin.mvvm.R
import com.teamlab.kotlin.mvvm.di.ApplicationComponent
import com.teamlab.kotlin.mvvm.model.Account
import com.teamlab.kotlin.mvvm.repository.AccountRepository
import javax.inject.Inject

class AccountActivity : AppCompatActivity() {

    @Inject lateinit var accountRepository: AccountRepository
    lateinit var account: Account

    companion object {
        private const val EXTRA_ACCOUNT_ID = "accountId"
        fun createIntent(context: Context, accountId: Long) = Intent(context, AccountActivity::class.java)
                .putExtra(EXTRA_ACCOUNT_ID, accountId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationComponent.from(this).inject(this)
        account = accountRepository.of(intent.getLongExtra(EXTRA_ACCOUNT_ID, -1))

        setContentView(R.layout.main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, TimelineFragment())
                    .commit()
        }
    }
}
