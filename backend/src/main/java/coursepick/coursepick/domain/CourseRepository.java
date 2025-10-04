package coursepick.coursepick.domain;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    void saveAll(Iterable<? extends Course> courses);

    List<Course> findAllHasDistanceWithin(Coordinate target, Meter distance);

    List<Course> findByIdIn(List<String> ids);

    Optional<Course> findById(String id);

    boolean existsByName(CourseName courseName);
}
