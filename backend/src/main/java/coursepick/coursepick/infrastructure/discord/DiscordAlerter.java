package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.Alerter;
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
public class DiscordAlerter implements Alerter {

    private final RestClient discordRestClient;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Async
    @Override
    public void alertCourse(Course course) {
        String message = """
                [%s] 코스 신고 알림
                - 코스 ID: %s
                - 코스 이름: %s
                - 신고 수: %d
                - 신고자 ID: %s
                """.formatted(
                activeProfile,
                course.id(),
                course.name().value(),
                course.reportUserIds().size(),
                course.reportUserIds()
        );
        sendMessage(message);
    }

    @Async
    @Override
    public void alertReview(Course course, Review review) {
        String message = """
                [%s] 리뷰 신고 알림
                - 코스 ID: %s
                - 코스 이름: %s
                - 리뷰 ID: %s
                - 리뷰 내용: %s
                - 신고 수: %d
                - 신고자 ID: %s
                """.formatted(
                activeProfile,
                course.id(),
                course.name().value(),
                review.id(),
                review.content(),
                review.reportUserIds().size(),
                review.reportUserIds()
        );
        sendMessage(message);
    }

    private void sendMessage(String message) {
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
