package io.coursepick.coursepick.presentation.coursedetail

data class Review(
    val id: String,
    val username: String,
    val isMine: Boolean,
    val rating: Float,
    val comment: String,
)
