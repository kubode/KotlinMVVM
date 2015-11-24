package com.teamlab.kotlin.mvvm.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.teamlab.kotlin.mvvm.MyApplication
import com.teamlab.kotlin.mvvm.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, CategoriesFragment())
                    .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as MyApplication).ref.watch(this)
    }
}
