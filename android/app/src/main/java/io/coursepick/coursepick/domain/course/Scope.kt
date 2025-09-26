package io.coursepick.coursepick.domain.course

@JvmInline
value class Scope private constructor(
    val meter: Meter,
) {
    companion object {
        private const val DEFAULT_SCOPE = 1_000

        fun default() = Scope(Meter(DEFAULT_SCOPE))

        operator fun invoke(meter: Int) = Scope(Meter(meter.coerceAtLeast(DEFAULT_SCOPE)))
    }
}
