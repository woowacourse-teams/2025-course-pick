package io.coursepick.coursepick.domain

data class Course(
    val id: Long,
    val name: CourseName,
    val distance: Distance,
    val length: Length,
    val coordinates: List<Coordinate>,
    val type: String?,
    val difficulty: CourseDifficulty,
) {
    init {
        require(coordinates.isNotEmpty())
    }
}
