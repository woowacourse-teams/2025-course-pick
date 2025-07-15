package coursepick.coursepick.application;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseApplicationService {

    private final CourseRepository courseRepository;

    public CourseApplicationService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findNearbyCourses(double latitude, double longitude) {
        return courseRepository.findAllHasDistanceLessThen(1000).stream()
                .sorted(Course.distanceComparator(latitude, longitude))
                .toList();
    }
}
