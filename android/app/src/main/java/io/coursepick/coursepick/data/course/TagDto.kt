package io.coursepick.coursepick.data.course

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val name: String,
    val label: String,
)
