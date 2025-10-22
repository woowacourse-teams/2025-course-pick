package io.coursepick.coursepick.data.course

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseService {
    @GET("/courses")
    suspend fun courses(
        @Query("mapLat") mapLatitude: Double,
        @Query("mapLng") mapLongitude: Double,
        @Query("userLat") userLatitude: Double?,
        @Query("userLng") userLongitude: Double?,
        @Query("scope") scopeMeter: Int,
    ): List<CourseDto>

    @GET("/courses/{id}/closest-coordinate")
    suspend fun nearestCoordinate(
        @Path("id") courseId: String,
        @Query("lat") currentLatitude: Double,
        @Query("lng") currentLongitude: Double,
    ): CoordinateDto
}
