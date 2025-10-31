package coursepick.coursepick.domain;

import coursepick.coursepick.application.exception.ErrorType;

import java.util.Arrays;

public enum Notice {
    VERIFIED_LOCATION(
            "verified_location",
            "/images/verified_location.png",
            "강남·송파 코스는 저희가 검증했어요\n 다른 지역은 아직 검증 중이에요 🏃",
            "* 메뉴 탭에서 다시 확인할 수 있어요."
    );

    private String id;
    private String imageUrl;
    private String title;
    private String description;

    Notice(String id, String imageUrl, String title, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
    }

    public static Notice findById(String id) {
        return Arrays.stream(Notice.values())
                .filter(notice -> notice.id.equals(id))
                .findFirst()
                .orElseThrow(ErrorType.NOT_FOUND_NOTICE::create);
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
