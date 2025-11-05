package coursepick.coursepick.application;

import coursepick.coursepick.domain.Course;

public interface CourseFileModifier {

    void modify(Course course);

    void delete(String courseId);
}
