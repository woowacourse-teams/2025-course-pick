package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseRepository
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude

class DefaultCourseRepository : CourseRepository {
    override suspend fun courses(
        latitude: Latitude,
        longitude: Longitude,
    ): List<Course> =
        Services.courseService
            .courses(latitude.value.toString(), longitude.value.toString())
            .mapNotNull { item: GetCoursesResponseItem ->
                item.toCourseOrNull()
            }
}
