package io.coursepick.coursepick.presentation.coursedetail

import kotlinx.serialization.Serializable

@Serializable
data class CourseReviewUiModel(
    val id: String,
    val authorId: String,
    val authorName: String,
    val isMine: Boolean,
    val rating: Float,
    val content: String,
)
