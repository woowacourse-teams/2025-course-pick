package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.AbstractAlerter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class DiscordAlerter extends AbstractAlerter {

    private final RestClient discordRestClient;

    @Async
    @Override
    protected void sendMessage(String message) {
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
