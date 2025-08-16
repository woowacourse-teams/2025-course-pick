package io.coursepick.coursepick.domain.course

@JvmInline
value class Scope private constructor(
    val meter: Int,
) {
    init {
        require(meter >= 0)
    }

    companion object {
        private const val DEFAULT_SCOPE = 1_000

        fun default() = Scope(DEFAULT_SCOPE)

        operator fun invoke(meter: Int) = Scope(meter.coerceAtLeast(DEFAULT_SCOPE))
    }
}
