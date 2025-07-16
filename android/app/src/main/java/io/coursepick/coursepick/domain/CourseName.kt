package io.coursepick.coursepick.domain

@JvmInline
value class CourseName(
    private val value: String,
) {
    init {
        require(value.length in MIN_LENGTH..MAX_LENGTH)
    }

    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 30
    }
}
