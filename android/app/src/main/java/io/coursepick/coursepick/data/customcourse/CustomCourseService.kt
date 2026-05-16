package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoursesPageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CustomCourseService {
    @POST("courses/draft/route")
    suspend fun draftSegment(
        @Body endpoints: EndpointsDto,
    ): DraftSegmentDto

    @POST("courses")
    suspend fun submitCourse(
        @Body course: DraftCourseDto,
    ): Response<Unit>

    @GET("courses/custom")
    suspend fun customCourses(
        @Query("userLat") userLatitude: Double?,
        @Query("userLng") userLongitude: Double?,
    ): Response<CoursesPageDto>
}
