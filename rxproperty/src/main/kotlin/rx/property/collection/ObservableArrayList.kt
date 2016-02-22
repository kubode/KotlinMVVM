package rx.property.collection

import rx.subjects.PublishSubject
import java.util.*

class ObservableArrayList<T> : ArrayList<T>(), ObservableCollection<T, List<T>> {

    @Transient private val notifier = PublishSubject.create<Notification<T, List<T>>>().toSerialized()

    override val observable = notifier.asObservable()

    private fun notifyInsert(start: Int, count: Int)
            = notifier.onNext(Notification(this, Notification.Type.INSERTED, start, count))

    private fun notifyChange(start: Int, count: Int)
            = notifier.onNext(Notification(this, Notification.Type.CHANGED, start, count))

    private fun notifyRemove(start: Int, count: Int)
            = notifier.onNext(Notification(this, Notification.Type.REMOVED, start, count))

    override fun add(element: T): Boolean {
        return super.add(element).apply { notifyInsert(size - 1, 1) }
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        notifyInsert(index, 1)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val oldSize = size
        return super.addAll(elements).apply { notifyInsert(oldSize, size - oldSize) }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return super.addAll(index, elements).apply { notifyInsert(index, elements.size) }
    }

    override fun set(index: Int, element: T): T {
        return super.set(index, element).apply { notifyChange(index, 1) }
    }

    override fun clear() {
        val oldSize = size
        super.clear()
        if (oldSize > 0) notifyRemove(0, oldSize)
    }

    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        return super.remove(element).apply { if (this) notifyRemove(index, 1) }
    }

    override fun removeAt(index: Int): T {
        return super.removeAt(index).apply { notifyRemove(index, 1) }
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        super.removeRange(fromIndex, toIndex)
        notifyRemove(fromIndex, toIndex - fromIndex)
    }
}
