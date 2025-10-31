package coursepick.coursepick.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    void save(Course course);

    void saveAll(Iterable<? extends Course> courses);

    Slice<Course> findAllHasDistanceWithin(Coordinate target, Meter distance, Pageable pageable);

    List<Course> findByIdIn(List<String> ids);

    Optional<Course> findById(String id);

    boolean existsByName(CourseName courseName);

    void delete(Course course);
}
