package io.coursepick.coursepick.domain.course

@JvmInline
value class Length(
    val meter: Meter,
) {
    init {
        require(meter >= 0)
    }

    constructor(meter: Int) : this(Meter(meter))

    companion object {
        operator fun invoke(meter: Double): Length = Length(Meter(meter))
    }
}
