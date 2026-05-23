package coursepick.coursepick.application;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.course.event.ReviewAddedEvent;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.AUTHENTICATION_FAIL;
import static coursepick.coursepick.application.exception.ErrorType.NOT_EXIST_COURSE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseCommandService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final Alerter alerter;
    private final CourseTagGenerator courseTagGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public void addCustomCourse(String name, List<Coordinate> coordinates, String userId) {
        CourseName courseName = new CourseName(name);
        validateDuplicatedCourseName(courseName);
        User user = getUser(userId);

        Course newCourse = new Course(null, courseName, coordinates, user);
        courseRepository.save(newCourse);
    }

    public void reportCourse(String courseId, String userId) {
        Course course = getCourse(courseId);
        User user = getUser(userId);

        course.addReport(user);
        courseRepository.save(course);

        if (course.isReportThreshold()) {
            alerter.alertCourse(course);
        }
    }

    public void addReview(String courseId, String userId, String content, int rating) {
        User user = getUser(userId);
        Course course = getCourse(courseId);
        course.verifyWriteReviewEligibility(user);

        courseRepository.pushReview(courseId, new Review(user, content, rating));
        eventPublisher.publishEvent(new ReviewAddedEvent(courseId));
    }

    public void deleteReview(String courseId, String reviewId, String userId) {
        Course course = getCourse(courseId);
        Review review = course.getReview(reviewId);
        course.verifyRemovableReview(review, userId);

        courseRepository.deleteReview(courseId, reviewId);
    }

    public void reportReview(String courseId, String reviewId, String userId) {
        User user = getUser(userId);
        Course course = getCourse(courseId);
        Review review = course.getReview(reviewId);

        review.addReport(user);
        courseRepository.save(course);

        alerter.alertReview(course, review);
    }

    public void regenerateTags(String courseId) {
        Course course = getCourse(courseId);
        if (course.reviews().isEmpty()) {
            return;
        }
        List<CourseTag> tags = courseTagGenerator.generate(course);
        course.updateTags(tags);
        courseRepository.save(course);
    }

    private void validateDuplicatedCourseName(CourseName courseName) {
        if (courseRepository.existByCourseName(courseName)) {
            throw ErrorType.DUPLICATED_COURSE_NAME.create(courseName.value());
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
