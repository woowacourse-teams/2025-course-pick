package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
import io.coursepick.coursepick.domain.customcourse.DraftCourse
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import javax.inject.Inject

class DefaultCustomCourseRepository
    @Inject
    constructor(
        private val service: CustomCourseService,
    ) : CustomCourseRepository {
        override suspend fun draftSegment(
            origin: Coordinate,
            destination: Coordinate,
        ): DraftSegment =
            service
                .draftSegment(
                    EndpointsDto(
                        origin = CoordinateDto(origin),
                        destination = CoordinateDto(destination),
                    ),
                ).toDraftSegment()

        override suspend fun course(draftCourse: DraftCourse): Course? = service.course(DraftCourseDto(draftCourse)).toCourseOrNull()
    }
