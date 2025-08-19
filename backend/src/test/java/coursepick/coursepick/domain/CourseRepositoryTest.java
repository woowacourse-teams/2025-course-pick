package coursepick.coursepick.domain;

import coursepick.coursepick.test_util.IntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import static coursepick.coursepick.test_util.CoordinateTestUtil.square;
import static coursepick.coursepick.test_util.CoordinateTestUtil.upright;
import static org.assertj.core.api.Assertions.assertThat;

class CourseRepositoryTest extends IntegrationTest {

    @Autowired
    CourseRepository sut;

    @ParameterizedTest
    @CsvSource({
            "1300, 4",
            "1100, 3",
            "900, 2",
            "700, 1"
    })
    void 거리를_줄여가면서_검색되는_코스_수가_줄어든다(int distance, int expectedSize) {
        var target = new Coordinate(37.514647, 127.086592);

        var course1 = new Course("코스1", square(upright(target, 300), 1000, 1000));
        var course2 = new Course("코스2", square(upright(target, 500), 1000, 1000));
        var course3 = new Course("코스3", square(upright(target, 700), 1000, 1000));
        var course4 = new Course("코스4", square(upright(target, 900), 1000, 1000));
        dbUtil.saveCourse(course1);
        dbUtil.saveCourse(course2);
        dbUtil.saveCourse(course3);
        dbUtil.saveCourse(course4);

        var courses = sut.findAllHasDistanceWithin(target, new Meter(distance));

        assertThat(courses).hasSize(expectedSize);
    }
}
