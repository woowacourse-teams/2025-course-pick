package coursepick.coursepick.domain;

import java.io.InputStream;
import java.util.List;

public interface CourseParser {

    List<Course> parse(InputStream fileStream);
}
