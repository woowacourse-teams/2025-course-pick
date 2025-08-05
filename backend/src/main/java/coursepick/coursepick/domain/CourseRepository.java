package coursepick.coursepick.domain;

import coursepick.coursepick.infrastructure.repository.CourseCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, Long>, CourseCustomRepository {

    List<Course> findAllHasDistanceWithin(Coordinate target, Meter length);

    Optional<Course> findById(String id);

    boolean existsByName(CourseName name);
}
