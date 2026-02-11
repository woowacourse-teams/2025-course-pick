package io.coursepick.coursepick.domain.course

@JvmInline
value class Kilometer(
    val value: Double,
) : Comparable<Kilometer> {
    constructor(value: Int) : this(value.toDouble())

    override fun compareTo(other: Kilometer): Int = this.value.compareTo(other.value)

    fun toMeter(): Meter = Meter(value * METRIC_MULTIPLIER / Meter.METRIC_MULTIPLIER)

    companion object {
        const val METRIC_MULTIPLIER = 1_000
    }
}
