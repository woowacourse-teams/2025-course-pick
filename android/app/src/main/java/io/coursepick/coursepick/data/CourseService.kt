package io.coursepick.coursepick.data

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
    ): List<CourseDto>

    @GET("/courses/{id}/closest-coordinate")
    suspend fun nearestCoordinate(
        @Path("id") courseId: Long,
        @Query("lat") currentLatitude: Double,
        @Query("lng") currentLongitude: Double,
    ): CoordinateDto
}
