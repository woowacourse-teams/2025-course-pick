package coursepick.coursepick.domain.notice;

import lombok.Getter;

@Getter
public enum Notice {
//    VERIFIED_LOCATION(
//            "verified_location",
//            "강남·송파 코스는 저희가 검증했어요\n 다른 지역은 아직 검증 중이에요 🏃",
//            "* 메뉴 탭에서 다시 확인할 수 있어요.",
//            "/verified_location.png",
//            null
//    ),
    ;
    private final String id;
    private final String title;
    private final String description;
    private final String imageUrl;
    private final String targetUrl;

    Notice(String id, String title, String description, String imageUrl, String targetUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.targetUrl = targetUrl;
    }
}
