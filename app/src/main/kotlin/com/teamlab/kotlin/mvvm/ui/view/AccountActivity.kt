package com.teamlab.kotlin.mvvm.ui.view

class AccountActivity : android.support.v7.app.AppCompatActivity() {

    @javax.inject.Inject lateinit var accountRepository: com.teamlab.kotlin.mvvm.data.repository.AccountRepository
    lateinit var account: com.teamlab.kotlin.mvvm.data.model.Account

    companion object {
        private const val EXTRA_ACCOUNT_ID = "accountId"
        fun createIntent(context: android.content.Context, accountId: Long) = android.content.Intent(context, AccountActivity::class.java)
                .putExtra(AccountActivity.Companion.EXTRA_ACCOUNT_ID, accountId)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        com.teamlab.kotlin.mvvm.di.ApplicationComponent.Companion.from(this).inject(this)
        account = accountRepository.of(intent.getLongExtra(AccountActivity.Companion.EXTRA_ACCOUNT_ID, -1))

        super.onCreate(savedInstanceState)
        setContentView(com.teamlab.kotlin.mvvm.R.layout.main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(com.teamlab.kotlin.mvvm.R.id.container, com.teamlab.kotlin.mvvm.ui.view.TimelineFragment())
                    .commit()
        }
    }
}
