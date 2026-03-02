package io.coursepick.coursepick.data.notice

import io.coursepick.coursepick.domain.notice.Notice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeDto(
    private val id: String,
    private val imageUrl: String,
    private val title: String,
    private val description: String,
    @SerialName("url") private val targetUrl: String,
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
