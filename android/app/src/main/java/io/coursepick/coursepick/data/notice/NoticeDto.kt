package io.coursepick.coursepick.data.notice

import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.domain.notice.Notice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeDto(
    private val id: String,
    @SerialName("imageUrl") private val imagePath: String,
    private val title: String,
    private val description: String,
) {
    fun toNotice(): Notice =
        Notice(
            id = id,
            imageUrl = "${BuildConfig.BASE_URL}$imagePath",
            title = title,
            description = description,
        )
}
