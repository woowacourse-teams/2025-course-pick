package io.coursepick.coursepick.data.course

import kotlinx.serialization.Serializable

@Serializable
data class ReviewOverviewDto(
    val reviewCount: Int,
    val averageRating: Float,
)
