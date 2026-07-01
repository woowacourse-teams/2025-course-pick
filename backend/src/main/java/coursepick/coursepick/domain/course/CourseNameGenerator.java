package coursepick.coursepick.domain.course;

import java.util.List;

public interface CourseNameGenerator {

    String generate(List<Coordinate> coordinates);
}
