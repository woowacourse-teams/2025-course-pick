package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository

class DefaultCourseRepository : CourseRepository {
    override suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
    ): List<Course> =
        Services.courseService
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
        Services.courseService
            .nearestCoordinate(
                courseId = selected.id,
                currentLatitude = current.latitude.value,
                currentLongitude = current.longitude.value,
            ).toCoordinate()
}
