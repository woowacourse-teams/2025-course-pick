package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.fixture.COURSE_FIXTURE_1
import io.coursepick.coursepick.domain.fixture.FAKE_COURSES

class FakeCourseRepository : CourseRepository {
    override suspend fun courses(courseIds: List<String>): List<Course> = FAKE_COURSES

    override suspend fun courses(
        scope: Scope,
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
    ): List<Course> = FAKE_COURSES

    override suspend fun routeToCourse(
        course: Course,
        origin: Coordinate,
    ): List<Coordinate> = COURSE_FIXTURE_1.segments.first().coordinates

    override suspend fun nearestCoordinate(
        selected: Course,
        origin: Coordinate,
    ): Coordinate = COORDINATE_FIXTURE
}
