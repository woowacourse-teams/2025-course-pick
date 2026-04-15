package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.domain.course.Discord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class WebHookDiscord implements Discord {

    private final RestClient discordRestClient;

    @Override
    public void alert(String message) {
        try {
            discordRestClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("content", message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Discord 알림 전송 실패: {}", e.getMessage());
        }
    }
}
