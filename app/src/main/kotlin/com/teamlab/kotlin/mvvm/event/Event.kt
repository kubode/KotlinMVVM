package com.teamlab.kotlin.mvvm.event

import com.github.kubode.rxeventbus.Event

class AddAccountEvent : Event()
class OpenUrlEvent(val url: String) : Event()
