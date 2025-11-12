package coursepick.coursepick.domain;

import coursepick.coursepick.application.exception.ErrorType;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum Notice {
    VERIFIED_LOCATION(
            "verified_location",
            "verified_location.png",
            "강남·송파 코스는 저희가 검증했어요\n 다른 지역은 아직 검증 중이에요 🏃",
            "* 메뉴 탭에서 다시 확인할 수 있어요."
    );

    private final String id;
    private final String imageFileName;
    private final String title;
    private final String description;

    Notice(String id, String imageFileName, String title, String description) {
        this.id = id;
        this.imageFileName = imageFileName;
        this.title = title;
        this.description = description;
    }

    public static Notice findById(String id) {
        return Arrays.stream(Notice.values())
                .filter(notice -> notice.id.equals(id))
                .findFirst()
                .orElseThrow(() -> ErrorType.NOT_FOUND_NOTICE.create(id));
    }
}
