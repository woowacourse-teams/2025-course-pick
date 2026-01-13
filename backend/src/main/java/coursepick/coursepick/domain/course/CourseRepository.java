package coursepick.coursepick.domain.course;

import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    Course save(Course course);

    void saveAll(Iterable<? extends Course> courses);

    Slice<Course> findAllHasDistanceWithin(CourseFindCondition condition);

    List<Course> findByIdIn(List<String> ids);

    Optional<Course> findById(String id);

    Optional<Course> findByName(CourseName courseName);

    void delete(Course course);
}
