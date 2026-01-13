package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.CoursesPage
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.fixture.COURSE_FIXTURE_1
import io.coursepick.coursepick.domain.fixture.FAKE_COURSES

class FakeCourseRepository : CourseRepository {
    var shouldThrowError: Boolean = false
    var customCoursesPage: CoursesPage? = null

    override suspend fun courses(courseIds: List<String>): List<Course> = FAKE_COURSES

    override suspend fun courses(
        scope: Scope,
        page: Int,
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
    ): CoursesPage {
        if (shouldThrowError) {
            throw RuntimeException("Fetch courses failed")
        }

        customCoursesPage?.let { coursesPage: CoursesPage -> return coursesPage }

        return when (page) {
            0 -> CoursesPage(courses = FAKE_COURSES, hasNext = true)
            1 -> CoursesPage(courses = emptyList(), hasNext = false)
            else -> CoursesPage(courses = emptyList(), hasNext = false)
        }
    }

    override suspend fun routeToCourse(
        course: Course,
        origin: Coordinate,
    ): List<Coordinate> = COURSE_FIXTURE_1.coordinates

    override suspend fun nearestCoordinate(
        selected: Course,
        origin: Coordinate,
    ): Coordinate = COORDINATE_FIXTURE
}
