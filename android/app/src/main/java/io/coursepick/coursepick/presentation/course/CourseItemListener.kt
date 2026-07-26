package io.coursepick.coursepick.presentation.course

interface CourseItemListener {
    fun select(course: CourseUiModel)

    fun toggleFavorite(course: CourseUiModel)

    fun navigateToCourse(course: CourseUiModel)

    fun navigateToDetail(course: CourseUiModel)
}
