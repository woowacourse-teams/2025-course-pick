package io.coursepick.coursepick.presentation.course

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.favorites.FavoriteCoursesFragment

enum class CoursesContent(
    @StringRes val headerId: Int,
    val fragmentClass: Class<out Fragment>,
) {
    EXPLORE(R.string.main_courses_header, ExploreCoursesFragment::class.java),
    FAVORITES(R.string.favorites_header, FavoriteCoursesFragment::class.java),
}
