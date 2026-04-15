package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.domain.course.Discord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Fallback
public class DummyDiscord implements Discord {

    @Override
    public void alert(String message) {
        log.info("[DummyDiscord] {}", message);
    }
}
