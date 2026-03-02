package io.coursepick.coursepick.data.notice

import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository
import javax.inject.Inject

class DefaultNoticeRepository
    @Inject
    constructor(
        private val service: NoticeService,
    ) : NoticeRepository {
        override suspend fun notices(): List<Notice> = service.notices().map(NoticeDto::toNotice)
    }
