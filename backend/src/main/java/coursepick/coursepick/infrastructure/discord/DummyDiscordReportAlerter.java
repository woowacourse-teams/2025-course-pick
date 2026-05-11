package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.ReportAlerter;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Fallback
public class DummyDiscordReportAlerter implements ReportAlerter {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Override
    public void alert(Course course) {
        String message = ReportMessageType.COURSE.createMessage(activeProfile, course, null);
        alert(message);
    }

    @Override
    public void alert(Course course, Review review) {
        String message = ReportMessageType.REVIEW.createMessage(activeProfile, course, review);
        alert(message);
    }

    private void alert(String message) {
        log.info("[DummyDiscordCourseReportAlerter] {}", message);
    }
}
