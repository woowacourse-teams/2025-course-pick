package io.coursepick.coursepick.domain.customcourse

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course

interface CustomCourseRepository {
    suspend fun draftSegment(
        origin: Coordinate,
        destination: Coordinate,
    ): DraftSegment

    suspend fun course(draftCourse: DraftCourse): Course
}
