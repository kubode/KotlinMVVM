package com.teamlab.kotlin.mvvm.event

import com.github.kubode.rxeventbus.Event
import com.teamlab.kotlin.mvvm.model.Account

class AddAccountEvent : Event()
class OpenUrlEvent(val url: String) : Event()
class AccountClickEvent(val account: Account) : Event()
class TweetEvent() : Event()
