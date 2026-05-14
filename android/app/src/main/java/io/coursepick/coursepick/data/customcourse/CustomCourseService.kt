package io.coursepick.coursepick.data.customcourse

import retrofit2.http.Body
import retrofit2.http.POST

interface CustomCourseService {
    @POST("courses/draft/route")
    suspend fun draftSegment(
        @Body endpoints: EndpointsDto,
    ): DraftSegmentDto

    @POST("courses")
    suspend fun submitCourse(
        @Body course: DraftCourseDto,
    )
}
