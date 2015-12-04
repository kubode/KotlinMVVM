package com.teamlab.kotlin.mvvm

abstract class Model<K>(val id: K) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Model<*>

        if (id != other.id) return false

        return true
    }

    final override fun hashCode() = id?.hashCode() ?: 0
    override fun toString() = "${javaClass.simpleName}($id)"
}
