package coursepick.coursepick.infrastructure.discord;

import coursepick.coursepick.application.Alerter;
import coursepick.coursepick.application.dto.AlertContext;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractAlerter implements Alerter {

    @Value("${spring.profiles.active:local}")
    protected String activeProfile;

    @Override
    public void alert(AlertContext alertContext) {
        String message = generateMessage(alertContext);
        sendMessage(message);
    }

    private String generateMessage(AlertContext alertContext) {
        return switch (alertContext.messageType()) {

            case COURSE -> alertContext.messageType().messageFormat().formatted(
                    activeProfile,
                    alertContext.commonContext().courseId(),
                    alertContext.commonContext().courseName(),
                    alertContext.commonContext().reportCount(),
                    alertContext.commonContext().reportUserIds()
            );

            case REVIEW -> alertContext.messageType().messageFormat().formatted(
                    activeProfile,
                    alertContext.commonContext().courseId(),
                    alertContext.commonContext().courseName(),
                    alertContext.reviewReportContext().reviewId(),
                    alertContext.reviewReportContext().content(),
                    alertContext.commonContext().reportCount(),
                    alertContext.commonContext().reportUserIds()
            );
        };
    }

    protected abstract void sendMessage(String message);
}
