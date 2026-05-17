package io.coursepick.coursepick.domain.customcourse

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.CoursesPage

interface CustomCourseRepository {
    suspend fun draftSegment(
        origin: Coordinate,
        destination: Coordinate,
    ): DraftSegment

    suspend fun submitCourse(course: DraftCourse)

    suspend fun customCourses(userCoordinate: Coordinate?): CoursesPage
}
