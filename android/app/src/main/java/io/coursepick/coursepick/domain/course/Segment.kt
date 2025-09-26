package io.coursepick.coursepick.domain.course

data class Segment(
    val inclineType: InclineType,
    val coordinates: List<Coordinate>,
) {
    init {
        require(coordinates.size >= MIN_COORDINATES_SIZE)
    }

    companion object {
        private const val MIN_COORDINATES_SIZE = 2
    }
}
