package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.data.Services
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository

class DefaultCourseRepository(
    private val service: CourseService,
) : CourseRepository {
    override suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
    ): List<Course> =
        service
            .courses(
                mapCoordinate.latitude.value,
                mapCoordinate.longitude.value,
                userCoordinate?.latitude?.value,
                userCoordinate?.longitude?.value,
            ).map(CourseDto::toCourse)

    override suspend fun nearestCoordinate(
        selected: Course,
        current: Coordinate,
    ): Coordinate =
        service
            .nearestCoordinate(
                courseId = selected.id,
                currentLatitude = current.latitude.value,
                currentLongitude = current.longitude.value,
            ).toCoordinate()
}
