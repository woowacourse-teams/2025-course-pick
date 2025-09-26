package io.coursepick.coursepick.domain.course

@JvmInline
value class Meter(
    val value: Int,
) : Comparable<Meter> {
    constructor(value: Double) : this(value.toInt())

    init {
        require(value > 0)
    }

    override fun compareTo(other: Meter): Int = this.value.compareTo(other.value)
}
