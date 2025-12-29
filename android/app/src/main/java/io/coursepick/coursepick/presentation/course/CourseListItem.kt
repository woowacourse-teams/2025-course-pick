package io.coursepick.coursepick.presentation.course

import android.R.attr.data

sealed class CourseListItem(
    itemViewType: ItemViewType,
) {
    val viewType: Int = itemViewType.ordinal

    data class Course(
        val item: CourseItem,
    ) : CourseListItem(ItemViewType.COURSE)

    data object Loading : CourseListItem(ItemViewType.LOADING)

    enum class ItemViewType {
        COURSE,
        LOADING,
    }
}
