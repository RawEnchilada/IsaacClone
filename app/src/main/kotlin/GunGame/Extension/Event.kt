package GunGame.Extension


typealias Observer<T,R> = (eventArgs: T) -> R

class Event<T,R> {
    private val subscribers = mutableListOf<Observer<T,R>>()

    operator fun plusAssign(subscriber: Observer<T,R>) {
        synchronized(subscribers) { subscribers.add(subscriber) }
    }

    operator fun minusAssign(observer: Observer<T,R>) {
        synchronized(subscribers) { subscribers.remove(observer) }
    }

    operator fun invoke(args: T) {
        subscribers.forEach { it.invoke(args) }
    }

    operator fun get(index:Int):Observer<T,R>{
        return subscribers[index];
    }

    val size get() = subscribers.size;
}