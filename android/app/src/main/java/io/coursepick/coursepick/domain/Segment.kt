package io.coursepick.coursepick.domain

data class Segment(
    val inclineType: InclineType,
    val coordinates: List<Coordinate>,
) {
    init {
        require(coordinates.size >= MIN_COORDINATES_SIZE)
    }

    companion object {
        const val MIN_COORDINATES_SIZE = 2
    }
}
