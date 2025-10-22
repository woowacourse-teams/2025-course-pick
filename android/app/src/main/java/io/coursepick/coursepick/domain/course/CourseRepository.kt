package io.coursepick.coursepick.domain.course

interface CourseRepository {
    suspend fun coursesById(courseIds: List<String>): List<Course>

    suspend fun courses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
        scope: Scope,
    ): List<Course>

    suspend fun routeToCourse(
        course: Course,
        origin: Coordinate,
    ): List<Coordinate>

    suspend fun nearestCoordinate(
        selected: Course,
        current: Coordinate,
    ): Coordinate
}
