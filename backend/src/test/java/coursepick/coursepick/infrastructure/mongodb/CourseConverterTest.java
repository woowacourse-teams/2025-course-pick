package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static coursepick.coursepick.test_util.CourseFixture.createCourse;
import static coursepick.coursepick.test_util.UserFixture.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CourseConverterTest extends AbstractIntegrationTest {

    private Course course;

    @BeforeEach
    void setUp() {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        course = createCourse("테스트 코스", List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)));
        ReflectionTestUtils.setField(course, "id", "507f1f77bcf86cd799439011");
        ReflectionTestUtils.setField(course, "simplifiedCoordinates", List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)));
        ReflectionTestUtils.setField(course, "length", new Meter(1500.0));
        ReflectionTestUtils.setField(course, "reviews", List.of(new Review(TEST_USER, "리뷰 내용", 4)));
        ReflectionTestUtils.setField(course, "creatorId", "creatorId123");
        ReflectionTestUtils.setField(course, "reportUserIds", Set.of("reportMan1"));
        ReflectionTestUtils.setField(course, "createdAt", now);
        ReflectionTestUtils.setField(course, "tags", List.of(CourseTag.NIGHT_VIEW, CourseTag.FLAT));
    }

    @Test
    void 코스_객체를_DB에_쓰고_다시_읽었을_때_데이터가_완전히_일치해야_한다() {
        dbUtil.saveCourse(course);
        var result = dbUtil.findCourseById(course.id());
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "reviews.createdAt") // 시간 필드는 별도 검증
                .isEqualTo(course);

        assertThat(result.createdAt()).isCloseTo(course.createdAt(), within(1, ChronoUnit.MILLIS));
        assertThat(result.reviews().getFirst().createdAt()).isCloseTo(course.reviews().getFirst().createdAt(), within(1, ChronoUnit.MILLIS));
    }

    @Test
    void createdAt이_null인_경우_ObjectId에서_추출하여_복원한다() {
        ReflectionTestUtils.setField(course, "createdAt", null);

        dbUtil.saveCourse(course);
        var result = dbUtil.findCourseById(course.id());

        assertThat(result.createdAt()).isNotNull();
    }
}
