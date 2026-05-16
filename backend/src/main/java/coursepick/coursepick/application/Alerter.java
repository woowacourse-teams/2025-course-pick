package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;

public interface Alerter {

    void alertCourse(Course course);

    void alertReview(Course course, Review review);
}
