package coursepick.coursepick.infrastructure.ai;

import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseTag;
import coursepick.coursepick.domain.course.CourseTagGenerator;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Fallback
public class DummyCourseTagGenerator implements CourseTagGenerator {

    @Override
    public List<CourseTag> generate(Course course) {
        return List.of();
    }
}
