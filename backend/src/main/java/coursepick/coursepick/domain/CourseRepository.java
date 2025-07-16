package coursepick.coursepick.domain;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface CourseRepository extends Repository<Course, Long> {

    List<Course> findAll();

    default List<Course> findAllHasDistanceLessThen(Coordinate target, int distance) {
        return findAll().stream()
                .filter(c -> c.minDistanceFrom(target) < distance)
                .toList();
    }
}
