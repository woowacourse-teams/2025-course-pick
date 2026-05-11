package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;

import org.jspecify.annotations.Nullable;

public enum ReportMessageType {

    COURSE("""
            [%s] 코스 신고 알림
            - 코스 ID: %s
            - 코스 이름: %s
            - 신고 수: %d
            - 신고자 ID: %s
            """
    ) {
        @Override
        public String createMessage(String profile, Course course, @Nullable Review review) {
            return messageFormat.formatted(
                    profile,
                    course.id(),
                    course.name().value(),
                    course.reportUserIds().size(),
                    course.reportUserIds()
            );
        }
    },
    REVIEW("""
            [%s] 리뷰 신고 알림
            - 코스 ID: %s
            - 코스 이름: %s
            - 리뷰 내용 : %s
            - 신고 수: %d
            - 신고자 ID: %s
            """
    ) {
        @Override
        public String createMessage(String profile, Course course, @Nullable Review review) {
            return messageFormat.formatted(
                    profile,
                    course.id(),
                    course.name().value(),
                    review.content(),
                    review.reportUserIds().size(),
                    review.reportUserIds()
            );
        }
    };

    final String messageFormat;

    ReportMessageType(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    abstract String createMessage(String profile, Course course, @Nullable Review review);
}
