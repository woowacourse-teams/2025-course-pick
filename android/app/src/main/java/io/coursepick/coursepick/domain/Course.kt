package io.coursepick.coursepick.domain

data class Course(
    val id: Long,
    val name: CourseName,
    val distance: Distance,
    val length: Length,
    val coordinates: List<Coordinate>,
) {
    init {
        require(coordinates.isNotEmpty())
    }
}
