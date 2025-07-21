package coursepick.coursepick.domain;

import java.util.List;

public interface CourseParser {

    List<Course> parse(String filePath);
}
