package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.CourseReview
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseReviewDto(
    @SerialName("id") val id: String,
    @SerialName("authorId") val authorId: String,
    @SerialName("authorNickname") val authorName: String,
    @SerialName("rating") val rating: Double,
    @SerialName("content") val content: String,
) {
    fun toCourseReview(): CourseReview =
        CourseReview(
            id = id,
            authorId = authorId,
            authorName = authorName,
            rating = rating,
            content = content,
        )
}
