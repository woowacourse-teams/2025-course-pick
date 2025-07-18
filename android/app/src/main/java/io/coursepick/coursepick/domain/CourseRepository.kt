package io.coursepick.coursepick.domain

interface CourseRepository {
    suspend fun courses(
        latitude: Latitude,
        longitude: Longitude,
    ): List<Course>
}
