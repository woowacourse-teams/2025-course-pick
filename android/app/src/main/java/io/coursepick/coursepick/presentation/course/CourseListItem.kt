package io.coursepick.coursepick.presentation.course

sealed class CourseListItem(
    itemViewType: ItemViewType,
) {
    val viewType: Int = itemViewType.ordinal

    data class Course(
        val item: CourseUiModel,
    ) : CourseListItem(ItemViewType.COURSE)

    data object Loading : CourseListItem(ItemViewType.LOADING)

    enum class ItemViewType {
        COURSE,
        LOADING,
    }
}
