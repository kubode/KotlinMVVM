package rx.mvvm

import rx.subjects.PublishSubject
import java.util.*

class ObservableArrayList<T> : ArrayList<T>() {

    data class Range(val start: Int, val count: Int)

    @Transient private val add = PublishSubject.create<Range>()
    @Transient private val change = PublishSubject.create<Range>()
    @Transient private val remove = PublishSubject.create<Range>()
    @Transient val addObservable = add.asObservable()
    @Transient val changeObservable = change.asObservable()
    @Transient val removeObservable = remove.asObservable()
    private fun notifyAdd(start: Int, count: Int) = add.onNext(Range(start, count))
    private fun notifyChange(start: Int, count: Int) = change.onNext(Range(start, count))
    private fun notifyRemove(start: Int, count: Int) = remove.onNext(Range(start, count))

    override fun add(element: T): Boolean {
        return super.add(element).apply { notifyAdd(size - 1, 1) }
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        notifyAdd(index, 1)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val oldSize = size
        return super.addAll(elements).apply { notifyAdd(oldSize, size - oldSize) }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return super.addAll(index, elements).apply { notifyAdd(index, elements.size) }
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
