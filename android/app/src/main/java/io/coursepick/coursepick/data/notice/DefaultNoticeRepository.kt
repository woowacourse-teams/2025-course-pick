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
            return listOf(
                Notice(
                    id = "1",
                    imageUrl = "https://avatars.githubusercontent.com/u/161921046?v=4",
                    title = "Notice 1 title",
                    description = "Notice 1 description",
                    noticeUrl = "https://github.com/woowacourse-teams/2025-course-pick",
                ),
                Notice(
                    id = "2",
                    imageUrl = "https://avatars.githubusercontent.com/u/192606356?v=4",
                    title = "Notice 2 title",
                    description = "Notice 2 description",
                    noticeUrl = "https://github.com/woowacourse-teams/2025-course-pick",
                ),
                Notice(
                    id = "3",
                    imageUrl = "https://avatars.githubusercontent.com/u/108331578?v=4",
                    title = "Notice 3 title",
                    description = "Notice 3 description",
                    noticeUrl = "https://github.com/woowacourse-teams/2025-course-pick",
                ),
                Notice(
                    id = "4",
                    imageUrl = "https://avatars.githubusercontent.com/u/176254419?v=4",
                    title = "Notice 4 title",
                    description = "Notice 4 description",
                    noticeUrl = "https://github.com/woowacourse-teams/2025-course-pick",
                ),
            )
//            return service.notices().map(NoticeDto::toNotice)
        }
    }
