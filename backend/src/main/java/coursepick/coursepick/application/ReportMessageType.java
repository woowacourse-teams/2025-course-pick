package coursepick.coursepick.application;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum ReportMessageType {

    COURSE("""
            [%s] 코스 신고 알림
            - 코스 ID: %s
            - 코스 이름: %s
            - 신고 수: %d
            - 신고자 ID: %s
            """
    ),
    REVIEW("""
            [%s] 리뷰 신고 알림
            - 코스 ID: %s
            - 코스 이름: %s
            - 리뷰 ID: %s
            - 리뷰 내용 : %s
            - 신고 수: %d
            - 신고자 ID: %s
            """
    );

    private final String messageFormat;

    ReportMessageType(String messageFormat) {
        this.messageFormat = messageFormat;
    }
}
