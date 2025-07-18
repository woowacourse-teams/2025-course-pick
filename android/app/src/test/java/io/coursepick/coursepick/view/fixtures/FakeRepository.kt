package io.coursepick.coursepick.view.fixtures

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

class FakeRepository : CourseRepository {
    override suspend fun courses(
        latitude: Latitude,
        longitude: Longitude,
    ): List<Course> = FAKE_COURSES
}
