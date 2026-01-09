package coursepick.coursepick.domain.course;

import org.springframework.data.repository.Repository;

public interface UserCreatedCourseRepository extends Repository<UserCreatedCourse, String> {

    void save(UserCreatedCourse userCreatedCourse);
}
