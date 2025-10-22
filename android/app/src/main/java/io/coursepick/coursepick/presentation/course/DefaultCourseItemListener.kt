package io.coursepick.coursepick.presentation.course

import android.Manifest
import androidx.annotation.RequiresPermission
import io.coursepick.coursepick.presentation.Logger

class DefaultCourseItemListener(
    val viewModel: CoursesViewModel,
) : CourseItemListener {
    override fun select(course: CourseItem) {
        Logger.log(
            Logger.Event.Click("course_on_list"),
            "id" to course.id,
            "name" to course.name,
        )
        viewModel.select(course)
    }

    override fun toggleFavorite(course: CourseItem) {
        viewModel.toggleFavorite(course)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun navigateToMap(course: CourseItem) {
        Logger.log(
            Logger.Event.Click("navigate"),
            "id" to course.id,
            "name" to course.name,
        )
    }
}
