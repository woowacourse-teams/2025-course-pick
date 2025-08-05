package io.coursepick.coursepick.view.fixtures

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository
import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.fixture.FAKE_COURSES

class FakeRepository : CourseRepository {
    override suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
    ): List<Course> = FAKE_COURSES

    override suspend fun nearestCoordinate(
        selected: Course,
        current: Coordinate,
    ): Coordinate = COORDINATE_FIXTURE
}
