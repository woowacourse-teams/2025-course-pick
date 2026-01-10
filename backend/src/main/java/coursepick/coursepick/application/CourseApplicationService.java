package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.domain.course.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.NOT_EXIST_COURSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private final CourseRepository courseRepository;
    private final RouteFinder routeFinder;

    @Transactional(readOnly = true)
    public CoursesResponse findNearbyCourses(CourseFindCondition condition, @Nullable Double userLatitude, @Nullable Double userLongitude) {
        Slice<Course> coursesWithinScope = courseRepository.findAllHasDistanceWithin(condition);
        return CoursesResponse.from(coursesWithinScope, createUserPositionOrNull(userLatitude, userLongitude));
    }

    private static Coordinate createUserPositionOrNull(@Nullable Double userLatitude, @Nullable Double userLongitude) {
        Coordinate coordinate = null;
        if (userLatitude != null && userLongitude != null) {
            coordinate = new Coordinate(userLatitude, userLongitude);
        }
        return coordinate;
    }

    @Transactional(readOnly = true)
    public List<Coordinate> routesToCourse(String id, double originLatitude, double originLongitude) {
        Coordinate destination = findClosestCoordinate(id, originLatitude, originLongitude);
        return routeFinder.find(new Coordinate(originLatitude, originLongitude), destination);
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
        loggingForNotExistsCourse(ids, courses);

        return courses.stream()
                .map(CourseResponse::from)
                .toList();
    }

    private void loggingForNotExistsCourse(List<String> ids, List<Course> courses) {
        for (Course course : courses) {
            if (!ids.contains(course.id())) {
                log.warn("존재하지 않는 코스에 대한 조회: {}", course.id());
            }
        }
    }
}
