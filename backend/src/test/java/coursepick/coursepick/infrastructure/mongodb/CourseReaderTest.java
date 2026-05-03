package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CourseReaderTest extends AbstractIntegrationTest {

    private Course course;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        course = new Course(
                "507f1f77bcf86cd799439011",
                new CourseName("테스트 코스"),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)),
                new Meter(1500.0),
                List.of(new Review(new User(null, "providerId", "reviewer"), "hi")),
                "creatorId123",
                Set.of("reportMan1"),
                now
        );
    }

    @Test
    void Document를_Course로_변환한다() {

        Course saved = dbUtil.saveCourse(course);
        Course result = dbUtil.findCourseById(saved.id());

        // then
        assertThat(result.id()).isEqualTo(course.id());
        assertThat(result.name()).isEqualTo(course.name());
        assertThat(result.coordinates()).isEqualTo(course.coordinates());
        assertThat(result.simplifiedCoordinates()).isEqualTo(course.simplifiedCoordinates());
        assertThat(result.length()).isEqualTo(course.length());

        // review는 nickname, content, createdAt만 직렬화되므로 세부 필드 검증
        List<Review> expectedReviews = course.reviews();
        assertThat(result.reviews()).satisfiesExactly(
                review -> {
                    assertThat(review.authorNickname()).isEqualTo(expectedReviews.getFirst().authorNickname());
                    assertThat(review.content()).isEqualTo(expectedReviews.getFirst().content());
                    assertThat(review.createdAt()).isNotNull();
                });

        assertThat(result.creatorId()).isEqualTo(course.creatorId());
        assertThat(result.reportUserIds()).containsExactlyInAnyOrderElementsOf(course.reportUserIds());
        assertThat(result.createdAt()).isEqualTo(course.createdAt());
    }

    @Test
    void createdAt이_null인_경우_ObjectId에서_추출한다() {
        Course course = new Course(
                "507f1f77bcf86cd799439011",
                new CourseName("테스트 코스"),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)),
                new Meter(1500.0),
                List.of(new Review(new User(null, "providerId", "reviewer"), "hi")),
                "creatorId123",
                Set.of("reportMan1"),
                null
        );

        Course saved = dbUtil.saveCourse(course);
        Course result = dbUtil.findCourseById(saved.id());

        assertThat(result.createdAt()).isNotNull();
    }
}
