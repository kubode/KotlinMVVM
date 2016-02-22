package rx.property.collection

data class Notification<T, C : Collection<T>>(val collection: C, val type: Type, val start: Int, val count: Int) {
    enum class Type { INSERTED, CHANGED, MOVED, REMOVED }
}
