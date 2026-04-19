package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.CourseReportAlerter;
import coursepick.coursepick.domain.course.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Fallback
public class DummyDiscord implements CourseReportAlerter {

    @Override
    public void alert(Course course) {
        String message = generateReportMessage(course);
        log.info("[DummyDiscord] {}", message);
    }

    private String generateReportMessage(Course course) {

        String reporterIds = String.join(", ", course.reportUserIds());
        return """
                [%s] 코스 신고 알림
                - 코스 ID: %s
                - 코스 이름: %si
                - 신고 수: %d
                - 신고자 ID: [%s]
                """.formatted("local", course.id(), course.name().value(), course.reportUserIds().size(), reporterIds);
    }
}
