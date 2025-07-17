package io.coursepick.coursepick.domain

@JvmInline
value class Distance(
    val meter: Int,
) {
    init {
        require(meter >= 0)
    }
}
