package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.event.ReviewAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseAiTagEventHandler {

    private final CourseApplicationService courseApplicationService;

    @Async
    @EventListener
    public void on(ReviewAddedEvent event) {
        try {
            courseApplicationService.regenerateTags(event.courseId());
        } catch (Exception e) {
            log.warn("AI 태그 갱신 실패 courseId={}", event.courseId(), e);
        }
    }
}
