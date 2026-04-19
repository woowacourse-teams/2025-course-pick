package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseDetailResponse;
import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.dto.CoursesResponse;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RouteFinder routeFinder;
    private final CourseReportAlerter courseReportAlerter;



    @Transactional
    public void addCustomCourse(String name, List<Coordinate> coordinates, String userId) {
        CourseName courseName = new CourseName(name);
        validateDuplicatedCourseName(courseName);
        User user = getUser(userId);

        Course newCourse = new Course(null, courseName, coordinates, user);
        courseRepository.save(newCourse);
    }

    @Transactional
    public void report(String courseId, String userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> NOT_EXIST_COURSE.create(courseId));
        User user = getUser(userId);

        course.addReport(user);
        courseRepository.save(course);

        if (course.isReportThreshold()) {
            courseReportAlerter.alert(course);
        }
    }

    private void validateDuplicatedCourseName(CourseName courseName) {
        if (courseRepository.existByCourseName(courseName)) {
            throw ErrorType.DUPLICATED_COURSE_NAME.create(courseName.value());
        }
    }

    @Transactional(readOnly = true)
    public CoursesResponse findNearbyCourses(CourseFindCondition condition, @Nullable Double userLatitude, @Nullable Double userLongitude) {
        Slice<Course> coursesWithinScope = courseRepository.findAllHasDistanceWithin(condition);
        return CoursesResponse.from(coursesWithinScope, createUserPositionOrNull(userLatitude, userLongitude));
    }

    private User getUser(String userId) {
        return userRepository.findById(userId).
                orElseThrow(() -> NOT_EXIST_USER.create(userId));
    }

    private static Coordinate createUserPositionOrNull(@Nullable Double userLatitude, @Nullable Double userLongitude) {
        Coordinate coordinate = null;
        if (userLatitude != null && userLongitude != null) {
            coordinate = new Coordinate(userLatitude, userLongitude);
        }
        return coordinate;
    }

    @Transactional(readOnly = true)
    public DraftSegment findDraftRoute(List<Coordinate> routePoints) {
        if (routePoints.size() < 2) {
            throw INVALID_COORDINATE_COUNT.create(routePoints.size());
        }
        DraftSegment draftRoute = DraftSegment.empty();
        for (int i = 0; i < routePoints.size() - 1; i++) {
            List<Coordinate> path = routeFinder.find(routePoints.get(i), routePoints.get(i + 1));
            draftRoute = draftRoute.merge(DraftSegment.of(path));
        }
        return draftRoute;
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

    @Transactional(readOnly = true)
    public CourseDetailResponse findCourseDetail(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> NOT_EXIST_COURSE.create(id));
        return CourseDetailResponse.from(course);
    }

    @Transactional
    public void addReview(String courseId, String userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(AUTHENTICATION_FAIL::create);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> NOT_EXIST_COURSE.create(courseId));
        course.addReview(user, content);
        courseRepository.save(course);
    }

    private void loggingForNotExistsCourse(List<String> ids, List<Course> courses) {
        for (Course course : courses) {
            if (!ids.contains(course.id())) {
                log.warn("존재하지 않는 코스에 대한 조회: {}", course.id());
            }
        }
    }
}
