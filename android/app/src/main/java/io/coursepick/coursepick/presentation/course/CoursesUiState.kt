package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.presentation.filter.CourseFilter

data class CoursesUiState(
    val originalCourses: List<CourseListItem>,
    val query: String = "",
    val status: UiStatus = UiStatus.Loading,
    val courseFilter: CourseFilter = CourseFilter.None,
    val verifiedLocations: Notice? = null,
    val showVerifiedLocations: Boolean = false,
    val notice: Notice? = null,
) {
    val isQueryBlank: Boolean = query.isBlank()
    val isFilterDefault: Boolean = courseFilter == CourseFilter.None
    val courses: List<CourseListItem> =
        originalCourses.filter { courseListItem: CourseListItem ->
            courseListItem is CourseListItem.Loading || (
                courseListItem is CourseListItem.Course &&
                    courseFilter.matches(
                        courseListItem.item,
                    )
            )
        }
}
