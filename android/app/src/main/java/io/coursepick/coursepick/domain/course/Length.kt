package io.coursepick.coursepick.domain.course

@JvmInline
value class Length(
    val meter: Meter,
) {
    init {
        require(meter >= 0)
    }

    constructor(meter: Double) : this(Meter(meter))

    companion object {
        operator fun invoke(meter: Int): Length = Length(Meter(meter))
    }
}
