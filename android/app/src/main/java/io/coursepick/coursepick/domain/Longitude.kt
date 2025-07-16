package io.coursepick.coursepick.domain

@JvmInline
value class Longitude(private val value: Int) {
    init {
        require(value in MIN_VALUE..<UPPER_BOUND)
    }

    companion object {
        private const val MIN_VALUE = -180
        private const val UPPER_BOUND = 180
    }
}
