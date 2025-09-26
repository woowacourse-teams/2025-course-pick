package io.coursepick.coursepick.domain.course

@JvmInline
value class Distance(
    val meter: Meter,
) : Comparable<Distance> {
    constructor(meter: Double) : this(Meter(meter))

    override fun compareTo(other: Distance): Int = this.meter.compareTo(other.meter)

    companion object {
        operator fun invoke(meter: Int): Distance = Distance(Meter(meter))
    }
}
