package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.CourseDetail
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDetailDto(
    @SerialName("id") val courseId: String,
    @SerialName("creatorId") val creatorId: String = "", // TODO: 백엔드 API 추가될 시 기본값 제거
    @SerialName("name") val name: String,
    @SerialName("length") val length: Double,
    @SerialName("coordinates") val coordinates: List<CoordinateDto>,
    @SerialName("reviewOverview") val reviewOverview: ReviewOverviewDto,
    @SerialName("tags") val tags: List<TagDto>,
    @SerialName("reviews") val reviews: List<CourseReviewDto>,
) {
    fun toCourseDetail(): CourseDetail =
        CourseDetail(
            courseId = courseId,
            creatorId = creatorId,
            name = CourseName(name),
            length = Length(length),
            coordinates = coordinates.map(CoordinateDto::toCoordinate),
            reviewCount = reviewOverview.reviewCount,
            averageRating = reviewOverview.averageRating,
            tags = tags.map(TagDto::name),
            reviews = reviews.map(CourseReviewDto::toCourseReview),
        )
}
