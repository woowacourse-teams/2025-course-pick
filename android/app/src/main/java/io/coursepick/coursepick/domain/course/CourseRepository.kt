package io.coursepick.coursepick.domain.course

interface CourseRepository {
    suspend fun courses(courseIds: List<String>): List<Course>

    suspend fun courses(
        scope: Scope,
        page: Int,
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate? = null,
        minLength: Meter? = null,
        maxLength: Meter? = null,
    ): CoursesPage

    suspend fun routeToCourse(
        course: Course,
        origin: Coordinate,
    ): List<Coordinate>

    suspend fun nearestCoordinate(
        selected: Course,
        origin: Coordinate,
    ): Coordinate

    suspend fun detail(courseId: String): CourseDetail

    suspend fun reportCourse(courseId: String)

    suspend fun submitReview(
        courseId: String,
        rating: Float,
        content: String,
    )
}
