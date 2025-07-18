package io.coursepick.coursepick.data

import kotlinx.serialization.Serializable

@Serializable
data class CourseResponse(
    val type: String?,
    val geometry: Geometry?,
    val properties: Properties?,
) {
    @Serializable
    data class Geometry(
        val type: String?,
        val coordinates: List<List<Double?>?>?,
    )

    @Serializable
    data class Properties(
        val name: String?,
        val distance: Double?,
        val length: Double?,
    )
}
