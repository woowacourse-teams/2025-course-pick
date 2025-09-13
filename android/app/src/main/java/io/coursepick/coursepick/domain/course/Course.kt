package io.coursepick.coursepick.domain.course

data class Course(
    val id: String,
    val name: CourseName,
    val distance: Distance?,
    val length: Length,
    val roadType: String,
    val difficulty: String,
    val inclineSummary: InclineSummary,
    val segments: List<Segment>,
) {
    init {
        require(segments.isNotEmpty())
    }
}
