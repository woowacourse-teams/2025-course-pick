package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CourseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomCourseService {
    @POST("/coordinates/snap")
    suspend fun draftSegment(
        @Body request: EndpointsDto,
    ): DraftSegmentDto

    @POST("/courses/create")
    suspend fun course(
        @Body request: DraftCourseDto,
    ): CourseDto
}
