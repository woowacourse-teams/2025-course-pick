package io.coursepick.coursepick.presentation.coursedetail

import kotlinx.serialization.Serializable

@Serializable
data class CourseDetailUiModel(
    val id: String,
    val name: String,
    val length: Double,
    val isFavorite: Boolean,
    val reviewCount: Int,
    val averageRating: Float,
    val tags: List<String>,
    val reviews: List<CourseReviewUiModel>,
)
