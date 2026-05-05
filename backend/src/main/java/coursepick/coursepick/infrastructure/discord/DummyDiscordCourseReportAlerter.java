package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.CourseReportAlerter;
import coursepick.coursepick.domain.course.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Fallback
public class DummyDiscordCourseReportAlerter implements CourseReportAlerter {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Override
    public void alert(Course course) {
        String message = generateReportMessage(course);
        log.info("[DummyDiscordCourseReportAlerter] {}", message);
    }

    private String generateReportMessage(Course course) {

        String reporterIds = String.join(", ", course.reportUserIds());
        return """
                [%s] 코스 신고 알림
                - 코스 ID: %s
                - 코스 이름: %s
                - 신고 수: %d
                - 신고자 ID: [%s]
                """.formatted(activeProfile, course.id(), course.name().value(), course.reportUserIds().size(), reporterIds);
    }
}
