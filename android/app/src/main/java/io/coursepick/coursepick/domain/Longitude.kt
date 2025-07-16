package io.coursepick.coursepick.domain

@JvmInline
value class Longitude(
    private val value: Double,
) {
    init {
        require(value in MIN_VALUE..<UPPER_BOUND)
    }

    companion object {
        private const val MIN_VALUE = -180.0
        private const val UPPER_BOUND = 180.0
    }
}
