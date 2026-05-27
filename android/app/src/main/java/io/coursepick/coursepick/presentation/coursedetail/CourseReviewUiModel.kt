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

fun CourseReview.toUiModel(): CourseReviewUiModel =
    CourseReviewUiModel(
        id = id,
        authorId = authorId,
        authorName = authorName,
        isMine = false, // TODO: API 업데이트될 시 사용자 ID 기반으로 확인하도록 변경
        rating = rating.toFloat(),
        content = content,
    )
