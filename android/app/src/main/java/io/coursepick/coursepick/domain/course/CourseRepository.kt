package io.coursepick.coursepick.domain.course

interface CourseRepository {
    suspend fun courses(courseIds: List<String>): List<Course>

    suspend fun courses(
        scope: Scope,
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
    ): List<Course>

    suspend fun courses(
        scope: Scope,
        page: Int,
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
    ): CoursesPage

    suspend fun routeToCourse(
        course: Course,
        origin: Coordinate,
    ): List<Coordinate>

    suspend fun nearestCoordinate(
        selected: Course,
        origin: Coordinate,
    ): Coordinate
}
