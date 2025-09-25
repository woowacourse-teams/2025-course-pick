package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.fixture.FAKE_COURSES

class FakeCourseRepository : CourseRepository {
    override suspend fun coursesById(courseIds: List<String>): List<Course> = FAKE_COURSES

    override suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
        scope: Scope,
    ): List<Course> = FAKE_COURSES

    override suspend fun nearestCoordinate(
        selected: Course,
        current: Coordinate,
    ): Coordinate = COORDINATE_FIXTURE
}
