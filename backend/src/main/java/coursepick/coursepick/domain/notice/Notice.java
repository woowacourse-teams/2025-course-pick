package coursepick.coursepick.domain.notice;

import lombok.Getter;

@Getter
public enum Notice {
    COURSE_ADDED(
            "feature_courseadd",
            "\"나의 코스\" 기능이 추가됐어요!",
            "직접 나만의 코스를 만들고 공유해보세요.",
            "/feature_courseadd.png",
            null
    ),
    COURSE_REVIEW(
            "feature_courseReview",
            "\"코스 리뷰\" 기능이 추가됐어요!",
            "러너들과 코스에 대한 후기를 나눠보세요 \n코스 목록 오른쪽에 있는 › 버튼을 눌러서 확인할 수 있어요.",
            "/feature_review.png",
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
