package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.AlertContext;
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
    private final Alerter alerter;

    @Transactional
    public void addCustomCourse(String name, List<Coordinate> coordinates, String userId) {
        CourseName courseName = new CourseName(name);
        validateDuplicatedCourseName(courseName);
        User user = getUser(userId);

        Course newCourse = new Course(null, courseName, coordinates, user);
        courseRepository.save(newCourse);
    }

    @Transactional
    public void reportCourse(String courseId, String userId) {
        Course course = getCourse(courseId);
        User user = getUser(userId);

        course.addReport(user);
        courseRepository.save(course);

        if (course.isReportThreshold()) {
//            alerter.alert(course);
            alerter.alert(AlertContext.fromCourseReport(course));
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

    @Transactional(readOnly = true)
    public CoursesResponse findCustomCourses(String userId, @Nullable Double userLatitude, @Nullable Double userLongitude) {
        List<Course> customCourses = courseRepository.findAllCustomCourses(userId);
        return CoursesResponse.from(customCourses, createUserPositionOrNull(userLatitude, userLongitude));
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
        List<Coordinate> coordinates = draftRoute.coordinates();
        return DraftSegment.of(coordinates.subList(1, coordinates.size() - 1));
    }

    @Transactional(readOnly = true)
    public List<Coordinate> routesToCourse(String id, double originLatitude, double originLongitude) {
        Coordinate destination = findClosestCoordinate(id, originLatitude, originLongitude);
        return routeFinder.find(new Coordinate(originLatitude, originLongitude), destination);
    }

    @Transactional(readOnly = true)
    public Coordinate findClosestCoordinate(String id, double latitude, double longitude) {
        Course course = getCourse(id);

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
        Course course = getCourse(id);
        return CourseDetailResponse.from(course);
    }

    @Transactional
    public void addReview(String courseId, String userId, String content) {
        User user = getUser(userId);
        Course course = getCourse(courseId);
        course.addReview(user, content);
        courseRepository.save(course);
    }

    @Transactional
    public void reportReview(String courseId, String reviewId, String userId) {
        User user = getUser(userId);
        Course course = getCourse(courseId);
        Review review = course.getReview(reviewId);

        review.addReport(user);
        courseRepository.save(course);

//        alerter.alert(course, review);
        alerter.alert(AlertContext.fromReviewReport(course, review));
    }

    private void loggingForNotExistsCourse(List<String> ids, List<Course> courses) {
        for (Course course : courses) {
            if (!ids.contains(course.id())) {
                log.warn("존재하지 않는 코스에 대한 조회: {}", course.id());
            }
        }
    }

    private User getUser(String userId) {
        return userRepository.findById(userId).
                orElseThrow(AUTHENTICATION_FAIL::create);
    }

    private Course getCourse(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> NOT_EXIST_COURSE.create(courseId));
    }
}
