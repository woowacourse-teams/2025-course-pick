package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
import io.coursepick.coursepick.domain.customcourse.DraftCourse
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import io.coursepick.coursepick.domain.customcourse.SubmitCourseResult
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
                        CoordinateDto(origin),
                        CoordinateDto(destination),
                    ),
                ).toDraftSegment()

        override suspend fun submitCourse(course: DraftCourse): SubmitCourseResult {
            val response = service.submitCourse(DraftCourseDto(course))
            return if (response.isSuccessful) {
                SubmitCourseResult.Success
            } else {
                when (response.code()) {
                    400 -> SubmitCourseResult.Failure.InvalidCourseName
                    401 -> SubmitCourseResult.Failure.UnauthorizedUser
                    else -> SubmitCourseResult.Failure.Unknown
                }
            }
        }
    }
