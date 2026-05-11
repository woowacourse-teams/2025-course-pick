package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CourseReaderTest extends AbstractIntegrationTest {

    private Course course;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        course = Course.testBuilder()
                .id("507f1f77bcf86cd799439011")
                .name(new CourseName("테스트 코스"))
                .coordinates(List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)))
                .simplifiedCoordinates(List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)))
                .length(new Meter(1500.0))
                .reviews(List.of(Review.testBuilder()
                        .authorNickname("reviewer")
                        .content("hi")
                        .createdAt(Instant.now())
                        .build()))
                .creatorId("creatorId123")
                .reportUserIds(Set.of("reportMan1"))
                .createdAt(now)
                .build();
    }

    @Test
    void Document를_Course로_변환한다() {

        Course saved = dbUtil.saveCourse(course);
        Course result = dbUtil.findCourseById(saved.id());

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
        Course course = Course.testBuilder()
                .id("507f1f77bcf86cd799439011")
                .name(new CourseName("테스트 코스"))
                .coordinates(List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)))
                .simplifiedCoordinates(List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)))
                .length(new Meter(1500.0))
                .reviews(new ArrayList<>())
                .creatorId("creatorId123")
                .reportUserIds(Set.of("reportMan1"))
                .createdAt(null)
                .build();

        Course saved = dbUtil.saveCourse(course);
        Course result = dbUtil.findCourseById(saved.id());

        assertThat(result.createdAt()).isNotNull();
    }
}
