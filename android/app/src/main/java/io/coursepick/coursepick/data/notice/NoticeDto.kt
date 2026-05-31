package io.coursepick.coursepick.data.notice

import io.coursepick.coursepick.domain.notice.Notice
import kotlinx.serialization.Serializable

@Serializable
data class NoticeDto(
    val id: String,
    val imageUrl: String,
    val targetUrl: String? = null,
    val title: String? = null,
    val description: String? = null,
) {
    fun toNotice(): Notice =
        Notice(
            id = id,
            imageUrl = imageUrl,
            title = title,
            description = description,
            targetUrl = targetUrl,
        )
}
