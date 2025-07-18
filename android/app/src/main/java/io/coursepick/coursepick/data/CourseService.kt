package io.coursepick.coursepick.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CourseService {
    @GET("/courses")
    suspend fun courses(
        @Query("lat") latitude: String,
        @Query("lng") longitude: String,
    ): List<CourseResponse>
}
