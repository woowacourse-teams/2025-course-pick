package io.coursepick.coursepick.presentation.course

sealed interface CourseListItem {
    data class Course(
        val item: CourseItem,
    ) : CourseListItem

    data object Loading : CourseListItem
}
