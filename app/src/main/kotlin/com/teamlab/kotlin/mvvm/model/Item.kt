package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.ReadOnlyObservableProperty

class Item(name: String) {
    val name = ReadOnlyObservableProperty(name)

    override fun toString(): String {
        return "name: ${name.value}"
    }
}
