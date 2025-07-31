package io.coursepick.coursepick.domain

@JvmInline
value class Length(
    val meter: Int,
) {
    init {
        require(meter >= 0)
    }

    constructor(meter: Double) : this(meter.toInt())
}
