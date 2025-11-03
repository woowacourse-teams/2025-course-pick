package io.coursepick.coursepick.domain.notice

interface NoticeRepository {
    suspend fun notice(id: String): Notice

    suspend fun verifiedLocations(): Notice
}
