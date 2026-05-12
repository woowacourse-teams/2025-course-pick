package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.AlertContext;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;

public interface Alerter {

    void alert(AlertContext alertContext);
}
