package io.coursepick.coursepick.domain.course

data class Course(
    val id: String,
    val name: CourseName,
    val distance: Distance?,
    val length: Length,
    val coordinates: List<Coordinate>,
) {
    init {
        require(coordinates.size >= MIN_COORDINATES_SIZE)
    }

    companion object {
        private const val MIN_COORDINATES_SIZE = 2
    }
}
