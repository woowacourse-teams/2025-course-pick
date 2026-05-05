package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.CourseReportAlerter;
import coursepick.coursepick.domain.course.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class DiscordCourseReportAlerter implements CourseReportAlerter {

    private final RestClient discordRestClient;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Async
    @Override
    public void alert(Course course) {
        String message = generateReportMessage(course);

        try {
            discordRestClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("content", message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Alerter 알림 전송 실패: {}", e.getMessage());
        }
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
