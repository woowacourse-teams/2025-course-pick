package io.coursepick.coursepick.data.course

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val rating: Float,
    val content: String,
)
