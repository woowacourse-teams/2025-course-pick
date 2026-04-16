package io.coursepick.coursepick.domain.course

@JvmInline
value class Length(
    val meter: Meter,
) : Comparable<Length> {
    init {
        require(meter >= 0)
    }

    constructor(meter: Int) : this(Meter(meter))

    override fun compareTo(other: Length): Int = meter.compareTo(other.meter)

    companion object {
        operator fun invoke(meter: Double): Length = Length(Meter(meter))
    }
}
