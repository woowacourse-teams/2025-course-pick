package io.coursepick.coursepick.data

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val coordinates: List<List<Double?>?>?,
    val type: String?,
)
