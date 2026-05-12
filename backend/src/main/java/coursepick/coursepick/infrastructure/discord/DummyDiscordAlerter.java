package coursepick.coursepick.infrastructure.discord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Fallback
public class DummyDiscordAlerter extends AbstractAlerter {

    @Override
    protected void sendMessage(String message) {
        log.info("[DummyDiscordCourseReportAlerter] {}", message);
    }
}

