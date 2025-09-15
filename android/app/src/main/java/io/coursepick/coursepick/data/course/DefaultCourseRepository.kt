package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.course.Scope

class DefaultCourseRepository(
    private val service: CourseService,
) : CourseRepository {
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
        listOf(
            Coordinate(Latitude(123.0), Longitude(123.0)),
            Coordinate(Latitude(124.0), Longitude(124.0)),
        )

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
