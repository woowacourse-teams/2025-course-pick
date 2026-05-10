package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;

public interface ReportAlerter {

    void alert(Course course);

    void alert(Course course, Review review);
}
