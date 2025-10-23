package io.coursepick.coursepick.data.course

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Scope
import javax.inject.Inject

class DefaultCourseRepository
    @Inject
    constructor(
        private val service: CourseService,
    ) : CourseRepository {
        override suspend fun courses(courseIds: List<String>): List<Course> =
            service.coursesById(courseIds).mapNotNull(CourseDto::toCourseOrNull)

        override suspend fun courses(
            scope: Scope,
            mapCoordinate: Coordinate,
            userCoordinate: Coordinate?,
        ): List<Course> =
            service
                .courses(
                    mapCoordinate.latitude.value,
                    mapCoordinate.longitude.value,
                    userCoordinate?.latitude?.value,
                    userCoordinate?.longitude?.value,
                    scope.meter.value.toInt(),
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
            origin: Coordinate,
        ): Coordinate =
            service
                .nearestCoordinate(
                    courseId = selected.id,
                    originLatitude = origin.latitude.value,
                    originLongitude = origin.longitude.value,
                ).toCoordinate()
    }
