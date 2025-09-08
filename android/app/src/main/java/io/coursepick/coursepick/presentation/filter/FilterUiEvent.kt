package io.coursepick.coursepick.presentation.filter

sealed interface FilterUiEvent {
    data object FilterCancel : FilterUiEvent

    data object FilterResult : FilterUiEvent
}
