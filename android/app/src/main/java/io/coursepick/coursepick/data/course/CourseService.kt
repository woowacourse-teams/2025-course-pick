package io.coursepick.coursepick.data.course

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseService {
    @GET("courses/favorites")
    suspend fun coursesById(
        @Query("courseIds") courseIds: List<String>,
    ): List<CourseDto>

    @GET("courses")
    suspend fun courses(
        @Query("mapLat") mapLatitude: Double,
        @Query("mapLng") mapLongitude: Double,
        @Query("userLat") userLatitude: Double?,
        @Query("userLng") userLongitude: Double?,
        @Query("scope") scopeMeter: Int,
        @Query("page") page: Int,
        @Query("minLength") minLengthMeter: Int? = null,
        @Query("maxLength") maxLengthMeter: Int? = null,
    ): CoursesPageDto

    @GET("courses/{id}/route")
    suspend fun routeToCourse(
        @Path("id") courseId: String,
        @Query("startLat") originLatitude: Double,
        @Query("startLng") originLongitude: Double,
    ): List<CoordinateDto>

    @GET("courses/{id}/closest-coordinate")
    suspend fun nearestCoordinate(
        @Path("id") courseId: String,
        @Query("lat") originLatitude: Double,
        @Query("lng") originLongitude: Double,
    ): CoordinateDto

    @GET("courses/{id}")
    suspend fun courseDetail(
        @Path("id") courseId: String,
    ): CourseDetailDto

    @POST("courses/{id}/report")
    suspend fun reportCourse(
        @Path("id") courseId: String,
    )

    @DELETE("courses/{courseId}/reviews/{reviewId}")
    suspend fun deleteReview(
        @Path("courseId") courseId: String,
        @Path("reviewId") reviewId: String,
    )

    @POST("courses/{courseId}/reviews/{reviewId}/report")
    suspend fun reportReview(
        @Path("courseId") courseId: String,
        @Path("reviewId") reviewId: String,
    )

    @POST("courses/{id}/reviews")
    suspend fun submitReview(
        @Path("id") courseId: String,
        @Body review: ReviewDto,
    )
}
