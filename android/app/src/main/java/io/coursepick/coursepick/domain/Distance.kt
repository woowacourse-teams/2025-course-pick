package io.coursepick.coursepick.domain

@JvmInline
value class Distance(
    private val meter: Int,
) {
    init {
        require(meter >= 0)
    }
}