package io.coursepick.coursepick.presentation.customcourse

data class DeleteCourseDialogState(
    val courseId: String,
    val courseName: String,
    val isDeleting: Boolean = false,
)
