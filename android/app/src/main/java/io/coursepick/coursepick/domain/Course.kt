package io.coursepick.coursepick.domain

data class Course(
    private val id: Long,
    private val name: CourseName,
    private val distance: Distance,
    private val length: Length,
    private val coordinates: List<Coordinate>
) {
    init {
        require(coordinates.isNotEmpty())
    }
}
