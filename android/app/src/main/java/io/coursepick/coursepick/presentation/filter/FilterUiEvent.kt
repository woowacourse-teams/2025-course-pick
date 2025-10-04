package io.coursepick.coursepick.presentation.filter

sealed interface FilterUiEvent {
    data object ResetFilter : FilterUiEvent

    data object CancelFilter : FilterUiEvent

    data class ApplyFilter(
        val courseFilter: CourseFilter,
    ) : FilterUiEvent
}
