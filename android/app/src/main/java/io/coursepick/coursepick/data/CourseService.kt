package io.coursepick.coursepick.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CourseService {
    @GET("/courses")
    suspend fun courses(
        @Query("mapLat") mapLatitude: Double,
        @Query("mapLng") mapLongitude: Double,
        @Query("userLat") userLatitude: Double?,
        @Query("userLng") userLongitude: Double?,
    ): List<CourseDto>
}
