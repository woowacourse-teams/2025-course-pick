package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.CourseFileModifier;
import coursepick.coursepick.domain.Course;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Fallback
@Component
public class DummyCourseFileModifier implements CourseFileModifier {

    @Override
    public void modify(Course course) {
        // 아무것도 하지 않는다.
    }

    @Override
    public void delete(String courseId) {
        // 아무것도 하지 않는다.
    }
}
