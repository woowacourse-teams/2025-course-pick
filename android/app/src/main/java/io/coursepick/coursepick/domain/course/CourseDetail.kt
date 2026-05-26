package io.coursepick.coursepick.domain.course

data class CourseDetail(
    val id: String,
    val name: CourseName,
    val length: Length,
    val coordinates: List<Coordinate>,
    val reviewCount: Int,
    val averageRating: Float,
    val tags: List<String>,
    val reviews: List<CourseReview>,
)
