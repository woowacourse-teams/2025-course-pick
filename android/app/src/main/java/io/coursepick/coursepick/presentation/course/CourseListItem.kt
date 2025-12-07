package io.coursepick.coursepick.presentation.course

sealed class CourseListItem {
    data class Course(
        val item: CourseItem,
    ) : CourseListItem()

    data object Loading : CourseListItem()
}
