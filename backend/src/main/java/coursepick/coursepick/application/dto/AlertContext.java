package coursepick.coursepick.application.dto;

import coursepick.coursepick.application.ReportMessageType;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

import java.util.Set;


@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlertContext {

    private final CommonContext commonContext;
    private final @Nullable ReviewReportContext reviewReportContext;
    private final ReportMessageType messageType;

    public static AlertContext fromCourseReport(Course course) {
        CommonContext commonContext = new CommonContext(
                course.id(),
                course.name().value(),
                course.reportUserIds().size(),
                course.reportUserIds()
        );

        return new AlertContext(commonContext, null, ReportMessageType.COURSE);
    }

    public static AlertContext fromReviewReport(Course course, Review review) {
        CommonContext commonContext = new CommonContext(
                course.id(),
                course.name().value(),
                review.reportUserIds().size(),
                review.reportUserIds()
        );
        ReviewReportContext reviewReportContext = new ReviewReportContext(review.id(), review.content());

        return new AlertContext(commonContext, reviewReportContext, ReportMessageType.REVIEW);
    }

    @Accessors(fluent = true)
    public record CommonContext(String courseId, String courseName, int reportCount, Set<String> reportUserIds) {
    }

    @Accessors(fluent = true)
    public record ReviewReportContext(String reviewId, String content) {
    }
}
