package io.coursepick.coursepick.domain.course

@JvmInline
value class Length(
    val meter: Meter,
) {
    constructor(meter: Double) : this(Meter(meter.toInt()))

    companion object {
        operator fun invoke(meter: Int): Length = Length(Meter(meter))
    }
}
