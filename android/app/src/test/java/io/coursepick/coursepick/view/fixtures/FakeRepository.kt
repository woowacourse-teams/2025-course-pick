package io.coursepick.coursepick.view.fixtures

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository

class FakeRepository : CourseRepository {
    override suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
    ): List<Course> = FAKE_COURSES
}
