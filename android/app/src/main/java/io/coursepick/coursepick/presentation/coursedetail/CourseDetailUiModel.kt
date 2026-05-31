package io.coursepick.coursepick.presentation.coursedetail

import io.coursepick.coursepick.domain.course.CourseDetail
import io.coursepick.coursepick.domain.course.CourseReview
import kotlinx.serialization.Serializable

@Serializable
data class CourseDetailUiModel(
    val id: String,
    val name: String,
    val length: Double,
    val reviewCount: Int,
    val averageRating: Float,
    val tags: List<String>,
    val reviews: List<CourseReviewUiModel>,
    val isFavorite: Boolean,
)

fun CourseDetail.toUiModel(
    isFavorite: Boolean,
    userId: String?,
) = CourseDetailUiModel(
    id = id,
    name = name.value,
    length = length.meter.value,
    reviewCount = reviewCount,
    averageRating = averageRating,
    tags = tags,
    reviews = reviews.map { review: CourseReview -> review.toUiModel(review.authorId == userId) },
    isFavorite = isFavorite,
)
