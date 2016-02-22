package rx.property.collection

import rx.Observable

interface ObservableCollection<T, C : Collection<T>> {
    val observable: Observable<Notification<T, C>>
}

val <T, C : Collection<T>> ObservableCollection<T, C>.insertedObservable: Observable<Notification<T, C>>
    get() = observable.filter { it.type == Notification.Type.INSERTED }
val <T, C : Collection<T>> ObservableCollection<T, C>.changedObservable: Observable<Notification<T, C>>
    get() = observable.filter { it.type == Notification.Type.CHANGED }
val <T, C : Collection<T>> ObservableCollection<T, C>.movedObservable: Observable<Notification<T, C>>
    get() = observable.filter { it.type == Notification.Type.MOVED }
val <T, C : Collection<T>> ObservableCollection<T, C>.removedObservable: Observable<Notification<T, C>>
    get() = observable.filter { it.type == Notification.Type.REMOVED }
