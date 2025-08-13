package io.coursepick.coursepick.presentation.ui

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
