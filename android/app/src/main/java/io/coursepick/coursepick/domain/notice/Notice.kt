package io.coursepick.coursepick.domain.notice

import java.io.Serializable

data class Notice(
    val id: String,
    val imageUrl: String,
    val targetUrl: String? = null,
    val title: String? = null,
    val description: String? = null,
) : Serializable
