package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository

class FakeNoticeRepository : NoticeRepository {
    private var mutedNoticeIds = emptySet<String>()

    override suspend fun notices(): List<Notice> = listOf(NOTICE_FIXTURE).filterNot { notice: Notice -> mutedNoticeIds.contains(notice.id) }

    override suspend fun muteNotice(noticeId: String) {
        mutedNoticeIds += noticeId
    }
}
