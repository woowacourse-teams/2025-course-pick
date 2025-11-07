package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository

class FakeNoticeRepository : NoticeRepository {
    override suspend fun notice(id: String): Notice =
        Notice(
            id = "",
            imageUrl = "",
            title =
                "강남·송파 코스는 저희가 검증했어요\n" +
                    "다른 지역은 아직 검증 중이에요 🏃",
            description = "* 메뉴 탭에서 다시 확인할 수 있어요.",
        )

    override suspend fun verifiedLocations(): Notice =
        Notice(
            id = "",
            imageUrl = "",
            title =
                "강남·송파 코스는 저희가 검증했어요\n" +
                    "다른 지역은 아직 검증 중이에요 🏃",
            description = "* 메뉴 탭에서 다시 확인할 수 있어요.",
        )
}
