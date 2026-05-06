package io.coursepick.coursepick.presentation.map

import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseItem

class CourseDiffHandler(
    private val onItemAdded: (CourseItem) -> Unit,
    private val onItemRemoved: (CourseItem) -> Unit,
) {
    private val courses = mutableSetOf<CourseItem>()

    fun updateCourses(newValue: Set<CourseItem>) {
        val removedCourses = courses.subtract(newValue)
        val addedCourses = newValue.subtract(courses)

        removedCourses.forEach { course: CourseItem ->
            courses.remove(course)
            onItemRemoved(course)
        }
        addedCourses.forEach { course: CourseItem ->
            courses.add(course)
            onItemAdded(course)
        }

        Logger.log(
            Logger.Event.Success("map_update_courses"),
            "count_raw" to newValue.size,
            "count_course_removed" to removedCourses.size,
            "count_course_added" to addedCourses.size,
        )
    }
}
