package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import coursepick.coursepick.domain.Meter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.NOT_EXIST_COURSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseResponse> findNearbyCourses(double mapLatitude, double mapLongitude, Double userLatitude, Double userLongitude, int scope) {
        final Coordinate mapPosition = new Coordinate(mapLatitude, mapLongitude);
        Meter meter = new Meter(scope).clamp(1000, 3000);

        List<Course> coursesWithinScope = courseRepository.findAllHasDistanceWithin(mapPosition, meter);

        if (userLatitude == null || userLongitude == null) {
            return coursesWithinScope
                    .stream()
                    .map(CourseResponse::from)
                    .toList();
        }

        final Coordinate userPosition = new Coordinate(userLatitude, userLongitude);
        return coursesWithinScope
                .stream()
                .map(course -> CourseResponse.from(course, userPosition))
                .toList();
    }

    @Transactional(readOnly = true)
    public Coordinate findClosestCoordinate(String id, double latitude, double longitude) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> NOT_EXIST_COURSE.create(id));

        return course.closestCoordinateFrom(new Coordinate(latitude, longitude));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findFavoriteCourses(List<String> ids) {
        List<Course> courses = courseRepository.findByIdIn(ids);
        return courses.stream()
                .map(CourseResponse::from)
                .toList();
    }
}
