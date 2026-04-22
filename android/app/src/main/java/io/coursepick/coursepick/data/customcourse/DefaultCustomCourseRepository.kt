package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.Result
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.customcourse.CustomCourseFailure
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
                        CoordinateDto(origin),
                        CoordinateDto(destination),
                    ),
                ).toDraftSegment()

        override suspend fun submitCourse(course: DraftCourse): Result<Unit, CustomCourseFailure> {
            val response = service.submitCourse(DraftCourseDto(course))
            return if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                when (response.code()) {
                    400 -> Result.Failure(CustomCourseFailure.InvalidCourseName)
                    401 -> Result.Failure(CustomCourseFailure.UnauthorizedUser)
                    409 -> Result.Failure(CustomCourseFailure.DuplicateCourseName)
                    else -> Result.Failure(CustomCourseFailure.Unknown)
                }
            }
        }
    }
