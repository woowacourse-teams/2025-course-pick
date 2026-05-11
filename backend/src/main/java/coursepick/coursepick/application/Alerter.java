package coursepick.coursepick.application;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;

public interface Alerter {

    void alert(Course course);

    void alert(Course course, Review review);
}
