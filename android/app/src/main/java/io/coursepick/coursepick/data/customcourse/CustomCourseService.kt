package io.coursepick.coursepick.data.customcourse

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CustomCourseService {
    @GET("")
    suspend fun draftSegment(
        @Query("") endpoints: EndpointsDto,
    ): DraftSegmentDto

    @POST("")
    suspend fun submitCourse(
        @Body course: DraftCourseDto,
    )
}
