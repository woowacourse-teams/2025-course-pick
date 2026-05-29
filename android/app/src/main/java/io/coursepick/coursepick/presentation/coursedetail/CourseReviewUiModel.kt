package io.coursepick.coursepick.presentation.coursedetail

import io.coursepick.coursepick.domain.course.CourseReview
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

fun CourseReview.toUiModel(isMine: Boolean) =
    CourseReviewUiModel(
        id = id,
        authorId = authorId,
        authorName = authorName,
        isMine = isMine,
        rating = rating.toFloat(),
        content = content,
    )
