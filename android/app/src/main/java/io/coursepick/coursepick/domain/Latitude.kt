package io.coursepick.coursepick.domain
@JvmInline
value class Latitude(private val value: Double) {
    init {
        require(value in MIN_VALUE .. MAX_VALUE)
    }

    companion object {
        private const val MIN_VALUE = -90.0
        private const val MAX_VALUE = 90.0
    }
}
