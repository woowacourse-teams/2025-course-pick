package io.coursepick.coursepick.domain

data class Course(
    val id: Long,
    val name: CourseName,
    val distance: Distance?,
    val length: Length,
    val roadType: String,
    val difficulty: String,
    val segments: List<Segment>,
) {
    init {
        require(segments.isNotEmpty())
    }
}
