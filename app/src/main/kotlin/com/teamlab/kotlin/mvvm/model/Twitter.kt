package com.teamlab.kotlin.mvvm.model

import twitter4j.TwitterFactory

class Twitter(private val pref: MyPreferences) {
    private val twitter = TwitterFactory.getSingleton()
    val oAuth = OAuth(twitter, pref)

    init {
        oAuth.initialize()
    }
}
