package io.coursepick.coursepick.data.notice

import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository
import javax.inject.Inject

class DefaultNoticeRepository
    @Inject
    constructor(
        private val service: NoticeService,
    ) : NoticeRepository {
        override suspend fun notices(): List<Notice> {
            return List(3) { i: Int ->
                Notice(
                    id = "$i",
                    imageUrl = "",
                    title = "$i",
                    description = "$i",
                )
            }
//            return service.notices().map(NoticeDto::toNotice)
        }
    }
