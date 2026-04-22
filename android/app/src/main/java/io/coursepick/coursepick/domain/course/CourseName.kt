package io.coursepick.coursepick.domain.course

@JvmInline
value class CourseName(
    val value: String,
) {
    init {
        require(value.length in MIN_LENGTH..MAX_LENGTH)
    }

    companion object {
        const val MIN_LENGTH = 2
        const val MAX_LENGTH = 30
    }
}
