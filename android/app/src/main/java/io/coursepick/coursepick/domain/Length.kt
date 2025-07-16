package io.coursepick.coursepick.domain

@JvmInline
value class Length(
    private val meter: Int,
) {
    init {
        require(meter >= 0)
    }
}
