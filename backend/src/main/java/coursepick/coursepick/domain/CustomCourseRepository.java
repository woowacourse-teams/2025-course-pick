package coursepick.coursepick.domain;

import java.util.List;

public interface CustomCourseRepository {

    List<Course> findAllHasDistanceWithin(Coordinate target, Meter meter);
}
