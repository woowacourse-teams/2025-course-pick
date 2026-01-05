package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository

class FakeNoticeRepository : NoticeRepository {
    override suspend fun notice(id: String): Notice = NOTICE_FIXTURE

    override suspend fun verifiedLocations(): Notice = NOTICE_FIXTURE
}
