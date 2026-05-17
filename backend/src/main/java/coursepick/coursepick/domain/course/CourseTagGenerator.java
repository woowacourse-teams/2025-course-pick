package coursepick.coursepick.domain.course;

import java.util.List;

public interface CourseTagGenerator {

    List<CourseTag> generate(Course course);
}
