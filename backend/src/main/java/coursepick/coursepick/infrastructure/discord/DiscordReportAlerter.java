package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.ReportAlerter;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;
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
public class DiscordReportAlerter implements ReportAlerter {

    private final RestClient discordRestClient;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Async
    @Override
    public void alert(Course course) {
        String message = ReportMessageType.COURSE.format(activeProfile, course.id(), course.name().value(), course.reportUserIds().size(), course.reportUserIds());
        alert(message);
    }

    @Async
    @Override
    public void alert(Course course, Review review) {
        String message = ReportMessageType.REVIEW.format(activeProfile, course.id(), course.name().value(), review.content(), review.reportUserIds().size(), review.reportUserIds());
        alert(message);
    }

    private void alert(String message) {
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
}
