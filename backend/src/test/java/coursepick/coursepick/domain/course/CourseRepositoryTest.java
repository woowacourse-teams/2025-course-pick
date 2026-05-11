package coursepick.coursepick.domain.course;

import coursepick.coursepick.infrastructure.discord.DiscordCourseReportAlerter;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static coursepick.coursepick.test_util.CoordinateTestUtil.square;
import static coursepick.coursepick.test_util.CoordinateTestUtil.upright;
import static coursepick.coursepick.test_util.CourseFixture.course;
import static org.assertj.core.api.Assertions.assertThat;

class CourseRepositoryTest extends AbstractIntegrationTest {

    final double mapLatitude = 37.514647;
    final double mapLongitude = 127.086592;

    @Autowired
    CourseRepository sut;

    @MockitoBean
    DiscordCourseReportAlerter discordCourseReportAlerter;

    @BeforeEach
    void setUp() {
        var target = new Coordinate(mapLatitude, mapLongitude);
        var course1 = course("코스1", square(upright(target, 1200), 10, 10)).build();
        var course2 = course("코스2", square(upright(target, 1500), 10, 10)).build();
        var course3 = course("코스3", square(upright(target, 1700), 10, 10)).build();
        var course4 = course("코스4", square(upright(target, 2000), 10, 10)).build();
        dbUtil.saveCourse(course1);
        dbUtil.saveCourse(course2);
        dbUtil.saveCourse(course3);
        dbUtil.saveCourse(course4);
    }

    @ParameterizedTest
    @CsvSource({
            "3000, 4",
            "2500, 3",
            "2300, 2",
            "2000, 1",
    })
    void 거리를_줄여가면서_검색되는_코스_수가_줄어든다(int scope, int expectedSize) {
        var condition = new CourseFindCondition(mapLatitude, mapLongitude, scope, null, null, null);

        var courses = sut.findAllHasDistanceWithin(condition);

        assertThat(courses).hasSize(expectedSize);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 4, false",
            "1, 0, false",
    })
    void 검색하는_코스를_페이징한다(int pageNumber, int expectedResultSize, boolean expectedHasNext) {
        var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, null, pageNumber);

        var courses = sut.findAllHasDistanceWithin(condition);

        assertThat(courses).hasSize(expectedResultSize);
        assertThat(courses.hasNext()).isEqualTo(expectedHasNext);
    }

    @Nested
    class 필터링_테스트 {

        @BeforeEach
        void setUp() {
            var target = new Coordinate(mapLatitude, mapLongitude);
            var shortCourse = course("짧은코스", square(target, 50, 50)).length(new Meter(200)).build();
            var mediumCourse = course("중간코스", square(target, 500, 500)).length(new Meter(2000)).build();
            var longCourse = course("긴코스", square(target, 2500, 2500)).length(new Meter(10000)).build();
            var veryLongCourse = course("매우긴코스", square(target, 7500, 7500)).length(new Meter(30000)).build();
            var easyCourse = course("쉬운코스", square(target, 100, 100)).length(new Meter(400)).build();
            var normalCourse = course("보통코스", square(target, 2000, 2000)).length(new Meter(8000)).build();
            var hardCourse = course("어려운코스", square(target, 4000, 4000)).length(new Meter(16000)).build();

            dbUtil.saveCourse(shortCourse);
            dbUtil.saveCourse(mediumCourse);
            dbUtil.saveCourse(longCourse);
            dbUtil.saveCourse(veryLongCourse);
            dbUtil.saveCourse(easyCourse);
            dbUtil.saveCourse(normalCourse);
            dbUtil.saveCourse(hardCourse);
        }

        @Test
        void 최소_길이로_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, 5000, null, null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> course.length().value() >= 5000);
        }

        @Test
        void 최대_길이로_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, 5000, null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> course.length().value() <= 5000);
        }

        @Test
        void 최소_최대_길이로_범위_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, 1000, 10000, null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> course.length().value() >= 1000 && course.length().value() <= 10000);
        }
    }
}
