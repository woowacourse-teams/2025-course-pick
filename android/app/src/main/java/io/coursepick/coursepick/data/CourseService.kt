package io.coursepick.coursepick.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CourseService {
    @GET("/courses")
    suspend fun courses(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
    ): List<CourseDto>
}
