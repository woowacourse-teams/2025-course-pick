package io.coursepick.coursepick.data

import kotlinx.serialization.Serializable

@Serializable
data class CoursesResponseItem(
    val geometry: Geometry?,
    val properties: Properties?,
    val type: String?,
)
