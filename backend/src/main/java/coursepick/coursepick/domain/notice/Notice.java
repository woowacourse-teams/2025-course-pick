package coursepick.coursepick.domain.notice;

import lombok.Getter;

@Getter
public enum Notice {
    VERIFIED_LOCATION(
            "feature_courseadd",
            "\"나의 코스\" 기능이 추가됐어요!",
            "직접 나만의 코스를 만들고 공유해보세요.",
            "/feature_courseadd.png",
            null
    ),
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
