package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseFile;
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

    private static final Meter SEARCH_RADIUS = new Meter(1000);

    private final CourseParserService courseParserService;
    private final CourseRepository courseRepository;

    @Transactional
    public void parseCourseFile(CourseFile file) {
        List<Course> courses = courseParserService.parse(file);
        courseRepository.saveAll(courses);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findNearbyCourses(double mapLatitude, double mapLongitude, Double userLatitude, Double userLongitude) {
        final Coordinate mapPosition = new Coordinate(mapLatitude, mapLongitude);
        if (userLatitude == null || userLongitude == null) {
            return courseRepository.findAllHasDistanceWithin(mapPosition, SEARCH_RADIUS)
                    .stream()
                    .map(CourseResponse::from)
                    .toList();
        }

        final Coordinate userPosition = new Coordinate(userLatitude, userLongitude);
        return courseRepository.findAllHasDistanceWithin(mapPosition, SEARCH_RADIUS)
                .stream()
                .map(course -> CourseResponse.from(course, userPosition))
                .toList();
    }

    @Transactional(readOnly = true)
    public Coordinate findClosestCoordinate(long id, double latitude, double longitude) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> NOT_EXIST_COURSE.create(id));

        return course.closestCoordinateFrom(new Coordinate(latitude, longitude));
    }
}
