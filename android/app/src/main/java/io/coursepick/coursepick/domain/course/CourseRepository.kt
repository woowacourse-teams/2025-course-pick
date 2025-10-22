package io.coursepick.coursepick.domain.course

interface CourseRepository {
    suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
        scope: Scope,
    ): List<Course>

    suspend fun nearestCoordinate(
        selected: Course,
        current: Coordinate,
    ): Coordinate
}
