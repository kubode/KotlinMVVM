package com.teamlab.kotlin.mvvm

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.teamlab.kotlin.mvvm.util.HasObjectGraph
import com.teamlab.kotlin.mvvm.util.ObjectGraph

class MyApplication : Application(), HasObjectGraph {
    override lateinit var objectGraph: ObjectGraph
    lateinit var ref: RefWatcher
    override fun onCreate() {
        super.onCreate()
        objectGraph = ObjectGraph()
                .add(MyApplicationModule(this))
        ref = LeakCanary.install(this)
    }
}
