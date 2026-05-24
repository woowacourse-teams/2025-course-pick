package io.coursepick.coursepick.data.notice

import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository
import javax.inject.Inject

class DefaultNoticeRepository
    @Inject
    constructor(
        private val service: NoticeService,
        private val dataSource: NoticeDataSource,
    ) : NoticeRepository {
        override suspend fun notices(): List<Notice> {
            val activeNotices: List<NoticeDto> = service.notices()

            val activeNoticeIds: Set<String> = activeNotices.map(NoticeDto::id).toSet()
            dataSource.removeStaleNoticeIds(activeNoticeIds)

            val mutedNoticeIds: Set<String> = dataSource.mutedNoticeIds()
            return activeNotices
                .filterNot { notice: NoticeDto -> mutedNoticeIds.contains(notice.id) }
                .map(NoticeDto::toNotice)
        }

        override suspend fun muteNotice(noticeId: String) {
            dataSource.addMutedNoticeId(noticeId)
        }
    }
