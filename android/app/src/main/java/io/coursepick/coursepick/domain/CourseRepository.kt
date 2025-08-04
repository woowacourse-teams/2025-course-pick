package io.coursepick.coursepick.domain

interface CourseRepository {
    suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
    ): List<Course>
}
