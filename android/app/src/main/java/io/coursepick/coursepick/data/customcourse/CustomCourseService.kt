package io.coursepick.coursepick.data.customcourse

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CustomCourseService {
    @GET("")
    suspend fun draftSegment(
        @Body endpoints: EndpointsDto,
    ): DraftSegmentDto

    @POST("")
    suspend fun submitCourse(
        @Body course: DraftCourseDto,
    )
}
