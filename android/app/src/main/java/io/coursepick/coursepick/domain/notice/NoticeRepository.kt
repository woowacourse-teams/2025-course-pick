package io.coursepick.coursepick.domain.notice

interface NoticeRepository {
    suspend fun notices(): List<Notice>
}
