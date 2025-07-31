package io.coursepick.coursepick.domain

@JvmInline
value class Distance(
    val meter: Int,
) : Comparable<Distance> {
    init {
        require(meter >= 0)
    }

    constructor(meter: Double) : this(meter.toInt())

    override fun compareTo(other: Distance): Int = this.meter.compareTo(other.meter)
}
