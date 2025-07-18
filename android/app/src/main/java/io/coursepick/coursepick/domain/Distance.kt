package io.coursepick.coursepick.domain

@JvmInline
value class Distance(
    val meter: Int,
) : Comparable<Distance> {
    init {
        require(meter >= 0)
    }

    override fun compareTo(other: Distance): Int = this.meter.compareTo(other.meter)
}
