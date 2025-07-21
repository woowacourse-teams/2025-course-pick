package coursepick.coursepick.domain;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface CourseRepository extends Repository<Course, Long> {

    List<Course> findAll();

    List<Course> saveAll(Iterable<Course> courses);

    default List<Course> findAllHasDistanceWithin(Coordinate target, Meter meter) {
        return findAll().stream()
                .filter(c -> c.minDistanceFrom(target).isWithin(meter))
                .toList();
    }
}
