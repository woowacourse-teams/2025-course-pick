package io.coursepick.coursepick.domain.course

@JvmInline
value class Meter(
    val value: Double,
) : Comparable<Meter> {
    constructor(value: Int) : this(value.toDouble())

    override fun compareTo(other: Meter): Int = this.value.compareTo(other.value)

    operator fun compareTo(other: Int): Int = this.value.compareTo(other)

    operator fun compareTo(other: Double): Int = this.value.compareTo(other)

    companion object {
        const val METRIC_MULTIPLIER = 1

        val MAX_VALUE = Meter(Double.MAX_VALUE)
    }
}
