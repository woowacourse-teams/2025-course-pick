package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.Alerter;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Fallback
public class DummyDiscordAlerter implements Alerter {

    @Override
    public void alertCourse(Course course) {
        log.info("[DummyDiscordAlerter] 코스 신고 알림 - courseId={}", course.id());
    }

    @Override
    public void alertReview(Course course, Review review) {
        log.info("[DummyDiscordAlerter] 리뷰 신고 알림 - courseId={}, reviewId={}", course.id(), review.id());
    }
}
