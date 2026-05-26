package io.coursepick.coursepick.domain.course

data class CourseReview(
    val id: String,
    val authorId: String,
    val authorName: String,
    val rating: Double,
    val content: String,
)
