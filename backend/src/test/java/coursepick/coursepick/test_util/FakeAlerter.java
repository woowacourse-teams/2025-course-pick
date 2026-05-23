package coursepick.coursepick.test_util;

import coursepick.coursepick.application.Alerter;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class FakeAlerter implements Alerter {
    private int reviewAlertCount = 0;
    private int courseAlertCount = 0;

    @Override
    public void alertCourse(Course course) {
        courseAlertCount++;
    }

    @Override
    public void alertReview(Course course, Review review) {
        reviewAlertCount++;
    }

    public int getReviewAlertCount() {
        return reviewAlertCount;
    }

    public int getCourseAlertCount() {
        return courseAlertCount;
    }

    public void reset() {
        reviewAlertCount = 0;
        courseAlertCount = 0;
    }
}
