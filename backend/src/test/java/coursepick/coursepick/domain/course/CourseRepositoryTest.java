package coursepick.coursepick.domain.course;

import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static coursepick.coursepick.test_util.CoordinateTestUtil.square;
import static coursepick.coursepick.test_util.CoordinateTestUtil.upright;
import static org.assertj.core.api.Assertions.assertThat;

class CourseRepositoryTest extends AbstractIntegrationTest {

    final double mapLatitude = 37.514647;
    final double mapLongitude = 127.086592;
    @Autowired
    CourseRepository sut;

    @BeforeEach
    void setUp() {
        var target = new Coordinate(mapLatitude, mapLongitude);
        var course1 = new Course("코스1", square(upright(target, 1200), 10, 10));
        var course2 = new Course("코스2", square(upright(target, 1500), 10, 10));
        var course3 = new Course("코스3", square(upright(target, 1700), 10, 10));
        var course4 = new Course("코스4", square(upright(target, 2000), 10, 10));
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
        var condition = new CourseFindCondition(mapLatitude, mapLongitude, scope, null, null, null, null);

        var courses = sut.findAllHasDistanceWithin(condition);
        for (Course course : courses) {
            System.out.println(course.name());
        }

        assertThat(courses).hasSize(expectedSize);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 4, false",
            "1, 0, false",
    })
    void 검색하는_코스를_페이징한다(int pageNumber, int expectedResultSize, boolean expectedHasNext) {
        var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, null, null, pageNumber);

        var courses = sut.findAllHasDistanceWithin(condition);

        assertThat(courses).hasSize(expectedResultSize);
        assertThat(courses.hasNext()).isEqualTo(expectedHasNext);
    }

    @Nested
    class 필터링_테스트 {

        @BeforeEach
        void setUp() {
            var target = new Coordinate(mapLatitude, mapLongitude);
            var shortCourse = new Course("짧은코스", RoadType.보도, square(target, 50, 50));      // 약 200m
            var mediumCourse = new Course("중간코스", RoadType.보도, square(target, 500, 500));    // 약 2000m
            var longCourse = new Course("긴코스", RoadType.보도, square(target, 2500, 2500));      // 약 10000m
            var veryLongCourse = new Course("매우긴코스", RoadType.보도, square(target, 7500, 7500)); // 약 30000m
            var easyCourse = new Course("쉬운코스", RoadType.보도, square(target, 100, 100));       // 약 400m, 쉬움
            var normalCourse = new Course("보통코스", RoadType.트레일, square(target, 2000, 2000)); // 약 8000m, 보통
            var hardCourse = new Course("어려운코스", RoadType.트레일, square(target, 4000, 4000));  // 약 16000m, 어려움

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
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, 5000, null, null, null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses).hasSize(2);
            assertThat(courses)
                    .extracting(Course::name)
                    .extracting(CourseName::value)
                    .containsExactlyInAnyOrder("긴코스", "매우긴코스");
        }

        @Test
        void 최대_길이로_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, 5000, null, null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses).hasSize(5);
            assertThat(courses)
                    .extracting(Course::name)
                    .extracting(CourseName::value)
                    .containsExactlyInAnyOrder("짧은코스", "중간코스", "쉬운코스", "보통코스", "어려운코스");
        }

        @Test
        void 최소_최대_길이로_범위_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, 1000, 10000, null, null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses).hasSize(3);
            assertThat(courses)
                    .extracting(Course::name)
                    .extracting(CourseName::value)
                    .containsExactlyInAnyOrder("중간코스", "보통코스", "어려운코스");
        }

        @Test
        void 쉬움_난이도로_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, null, List.of("easy"), null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> course.difficulty() == Difficulty.쉬움);
        }

        @Test
        void 보통_난이도로_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, null, List.of("normal"), null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> course.difficulty() == Difficulty.보통);
        }

        @Test
        void 어려움_난이도로_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, null, List.of("hard"), null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> course.difficulty() == Difficulty.어려움);
        }

        @Test
        void 여러_난이도로_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, null, null, List.of("easy", "normal"), null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> course.difficulty() == Difficulty.쉬움 || course.difficulty() == Difficulty.보통);
        }

        @Test
        void 길이와_난이도를_함께_필터링한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, 1000, 10000, List.of("normal"), null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses).hasSize(1);
            assertThat(courses)
                    .extracting(Course::name)
                    .extracting(CourseName::value)
                    .containsExactly("보통코스");
        }

        @Test
        void 모든_필터를_함께_사용한다() {
            var condition = new CourseFindCondition(mapLatitude, mapLongitude, 3000, 5000, 20000, List.of("normal", "hard"), null);

            var courses = sut.findAllHasDistanceWithin(condition);

            assertThat(courses)
                    .allMatch(course -> {
                        boolean lengthInRange = course.length().value() >= 5000 && course.length().value() <= 20000;
                        boolean difficultyMatch = course.difficulty() == Difficulty.보통 || course.difficulty() == Difficulty.어려움;
                        return lengthInRange && difficultyMatch;
                    });
        }
    }
}
