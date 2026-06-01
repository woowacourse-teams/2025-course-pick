package coursepick.coursepick.domain.course;

import java.util.List;

public record ParsedCourses(
        List<Course> courses,
        List<String> skippedReasons
) {
}
