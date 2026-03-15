package io.coursepick.coursepick.domain.location

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Distance

sealed interface Location {
    val coordinate: Coordinate

    data class Fine(
        override val coordinate: Coordinate,
    ) : Location

    data class Coarse(
        override val coordinate: Coordinate,
        val accuracy: Distance,
    ) : Location
}
