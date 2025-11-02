package io.coursepick.coursepick.domain.notice

import java.io.Serializable

data class Notice(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String,
) : Serializable
