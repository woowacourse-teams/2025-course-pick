package io.coursepick.coursepick.presentation.course

sealed class CourseListItem {
    data class Course(val courseItem: CourseItem) : CourseListItem()

    object Loading : CourseListItem()
}
