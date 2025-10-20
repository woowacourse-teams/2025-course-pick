package io.coursepick.coursepick.presentation.course

sealed class UiStatus {
    data object Loading : UiStatus()

    data object Success : UiStatus()

    data object Failure : UiStatus()

    data object NoInternet : UiStatus()
}
