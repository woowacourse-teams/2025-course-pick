package io.coursepick.coursepick.data

import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    val distance: Double?,
    val length: Double?,
    val name: String?,
)
