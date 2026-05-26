package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.CourseDetail
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import kotlinx.serialization.Serializable

@Serializable
data class CourseDetailDto(
    val id: String,
    val name: String,
    val length: Double,
    val coordinates: List<CoordinateDto>,
    val reviewOverview: ReviewOverviewDto,
    val tags: List<TagDto>,
    val reviews: List<CourseReviewDto>,
) {
    fun toCourseDetailOrNull(): CourseDetail? =
        runCatching {
            CourseDetail(
                id = id,
                name = CourseName(name),
                length = Length(length),
                coordinates = coordinates.map(CoordinateDto::toCoordinate),
                reviewCount = reviewOverview.reviewCount,
                averageRating = reviewOverview.averageRating,
                tags = tags.map(TagDto::name),
                reviews = reviews.map(CourseReviewDto::toCourseReview),
            )
        }.getOrNull()
}
