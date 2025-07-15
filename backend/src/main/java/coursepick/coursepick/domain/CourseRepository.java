package coursepick.coursepick.domain;

import java.util.List;

public interface CourseRepository {

    List<Course> findAllHasDistanceLessThen(int distance);
}
