package io.coursepick.coursepick.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

abstract class SingleLiveData<T> {
    private val liveData = MutableLiveData<Event<T>>()

    protected constructor()

    protected constructor(value: T) {
        liveData.value = Event(value)
    }

    open var value: T?
        get() = liveData.value?.content
        protected set(value) {
            liveData.value = value?.let { Event(it) }
        }

    protected open fun postValue(value: T) {
        liveData.postValue(Event(value))
    }

    fun observe(
        owner: LifecycleOwner,
        onResult: (T) -> Unit,
    ) {
        liveData.observe(owner) { it.getContentIfNotHandled()?.let(onResult) }
    }
}

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
private class Event<out T>(
    val content: T,
) {
    var hasBeenHandled = false
        private set

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
}
