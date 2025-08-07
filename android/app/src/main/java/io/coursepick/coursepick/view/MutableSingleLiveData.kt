package io.coursepick.coursepick.view

class MutableSingleLiveData<T> : SingleLiveData<T> {
    constructor() : super()

    constructor(value: T) : super(value)

    public override fun postValue(value: T) {
        super.postValue(value)
    }

    public override var value: T?
        get() = super.value
        set(value) {
            super.value = value
        }
}
