package com.teamlab.kotlin.mvvm.data.factory

import com.teamlab.kotlin.mvvm.di.ApplicationScope
import twitter4j.Twitter
import twitter4j.TwitterFactory
import javax.inject.Inject

@ApplicationScope
class MyTwitterFactory @Inject constructor() {
    private val CONSUMER_KEY = "hZOuxdz51CludNkjEyMuiR6JB"
    private val CONSUMER_SECRET = "2gtKvKETxmDlj0P9VNrzUQgm3JY6m8To1Trd5lqU8sOsPsyRMh"

    fun create(): Twitter {
        return TwitterFactory().instance.apply {
            setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET)
        }
    }
}
