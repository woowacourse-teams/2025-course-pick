package io.coursepick.coursepick.presentation.coursedetail

import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length

data class CourseDetail(
    val id: String,
    val courseName: CourseName,
    val length: Length,
    val averageRating: Float,
    val isFavorite: Boolean,
    val reviewCount: Int,
)
