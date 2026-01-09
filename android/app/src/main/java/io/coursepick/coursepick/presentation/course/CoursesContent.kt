package io.coursepick.coursepick.presentation.course

import androidx.fragment.app.Fragment
import io.coursepick.coursepick.presentation.customcourse.CustomCourseFragment
import io.coursepick.coursepick.presentation.favorites.FavoriteCoursesFragment

enum class CoursesContent(
    val fragmentClass: Class<out Fragment>,
) {
    EXPLORE(ExploreCoursesFragment::class.java),
    FAVORITES(FavoriteCoursesFragment::class.java),
    CUSTOM_COURSE(CustomCourseFragment::class.java),
}
