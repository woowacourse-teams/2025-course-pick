package io.coursepick.coursepick.domain

data class Segment(
    val inclineType: InclineType,
    val coordinates: List<Coordinate>,
) {
    init {
        require(coordinates.isNotEmpty())
    }
}
