package io.coursepick.coursepick.domain.customcourse

import io.coursepick.coursepick.domain.course.Coordinate

interface CustomCourseRepository {
    suspend fun draftSegment(
        origin: Coordinate?,
        destination: Coordinate,
    ): DraftSegment

    suspend fun submitCourse(course: DraftCourse)
}
