package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Scope

class DefaultCourseRepository(
    private val service: CourseService,
) : CourseRepository {
    override suspend fun coursesById(courseIds: List<String>): List<Course> =
        service.coursesById(courseIds).mapNotNull(CourseDto::toCourseOrNull)

    override suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
        scope: Scope,
    ): List<Course> =
        service
            .courses(
                mapCoordinate.latitude.value,
                mapCoordinate.longitude.value,
                userCoordinate?.latitude?.value,
                userCoordinate?.longitude?.value,
                scope.meter,
            ).mapNotNull(CourseDto::toCourseOrNull)

    override suspend fun routeToCourse(
        course: Course,
        origin: Coordinate,
    ): List<Coordinate> =
        service
            .routeToCourse(
                course.id,
                origin.latitude.value,
                origin.longitude.value,
            ).map(CoordinateDto::toCoordinate)

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
