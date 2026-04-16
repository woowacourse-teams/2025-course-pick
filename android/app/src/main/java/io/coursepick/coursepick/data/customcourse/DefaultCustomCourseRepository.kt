package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Length
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
        ): DraftSegment {
            return DraftSegment(listOf(origin, destination), Length(1))
//            return service
//                .draftSegment(
//                    EndpointsDto(
//                        CoordinateDto(origin),
//                        CoordinateDto(destination),
//                    ),
//                ).toDraftSegment()
        }

        override suspend fun submitCourse(course: DraftCourse) {
//            service.submitCourse(DraftCourseDto(course))
        }
    }
