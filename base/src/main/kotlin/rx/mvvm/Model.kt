package rx.mvvm

abstract class Model<K : Any> {

    // Do NOT implements as var property
    abstract val id: K

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Model<*>
        if (id != other.id) return false
        return true
    }

    final override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "${javaClass.simpleName}($id)"
    }
}
