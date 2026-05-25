package io.coursepick.coursepick.data.notice

import kotlinx.serialization.Serializable

@Serializable
data class NoticeResponseDto(
    val notices: List<NoticeDto>,
)
