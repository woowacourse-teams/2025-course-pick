package io.coursepick.coursepick.presentation.map

import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseUiModel

class CourseDiffHandler(
    private val onItemAdded: (CourseUiModel) -> Unit,
    private val onItemRemoved: (CourseUiModel) -> Unit,
) {
    private val courses = mutableSetOf<CourseUiModel>()

    fun updateCourses(newValue: Set<CourseUiModel>) {
        val removedCourses = courses.subtract(newValue)
        val addedCourses = newValue.subtract(courses)

        removedCourses.forEach { course: CourseUiModel ->
            courses.remove(course)
            onItemRemoved(course)
        }
        addedCourses.forEach { course: CourseUiModel ->
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
