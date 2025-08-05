package coursepick.coursepick.infrastructure.repository;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.Meter;

import java.util.List;

public interface CourseCustomRepository {

    List<Course> findAllHasDistanceWithin(Coordinate target, Meter length);
}
