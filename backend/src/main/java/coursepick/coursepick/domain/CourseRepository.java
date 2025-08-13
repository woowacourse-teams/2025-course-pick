package coursepick.coursepick.domain;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends Repository<Course, String> {

    void save(Course course);

    void saveAll(Iterable<Course> courses);

    List<Course> findAll();

    Optional<Course> findById(String id);

    default List<Course> findAllHasDistanceWithin(Coordinate target, Meter meter) {
        return findAll().stream()
                .filter(c -> c.distanceFrom(target).isWithin(meter))
                .toList();
    }

    boolean existsByName(CourseName courseName);
}
