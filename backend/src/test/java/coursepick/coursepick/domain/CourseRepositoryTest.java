package coursepick.coursepick.domain;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseRepository;
import coursepick.coursepick.domain.course.Meter;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static coursepick.coursepick.test_util.CoordinateTestUtil.square;
import static coursepick.coursepick.test_util.CoordinateTestUtil.upright;
import static org.assertj.core.api.Assertions.assertThat;

class CourseRepositoryTest extends AbstractIntegrationTest {

    final Coordinate target = new Coordinate(37.514647, 127.086592);
    @Autowired
    CourseRepository sut;

    @BeforeEach
    void setUp() {
        var course1 = new Course("코스1", square(upright(target, 300), 1000, 1000));
        var course2 = new Course("코스2", square(upright(target, 500), 1000, 1000));
        var course3 = new Course("코스3", square(upright(target, 700), 1000, 1000));
        var course4 = new Course("코스4", square(upright(target, 900), 1000, 1000));
        dbUtil.saveCourse(course1);
        dbUtil.saveCourse(course2);
        dbUtil.saveCourse(course3);
        dbUtil.saveCourse(course4);
    }

    @ParameterizedTest
    @CsvSource({
            "1300, 4",
            "1100, 3",
            "900, 2",
            "700, 1"
    })
    void 거리를_줄여가면서_검색되는_코스_수가_줄어든다(int distance, int expectedSize) {
        var courses = sut.findAllHasDistanceWithin(target, new Meter(distance), PageRequest.of(0, 100));

        assertThat(courses).hasSize(expectedSize);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2, 2, true",
            "1, 2, 2, false",
            "0, 4, 4, false",
            "0, 5, 4, false"
    })
    void 검색하는_코스를_페이징한다(int pageNumber, int pageSize, int expectedResultSize, boolean expectedHasNext) {
        var courses = sut.findAllHasDistanceWithin(target, new Meter(1500), PageRequest.of(pageNumber, pageSize));

        assertThat(courses).hasSize(expectedResultSize);
        assertThat(courses.hasNext()).isEqualTo(expectedHasNext);
    }

    @Test
    void PageRequest가_널이면_페이징하지_않는다() {
        var courses = sut.findAllHasDistanceWithin(target, new Meter(1500), null);

        assertThat(courses).hasSize(4);
        assertThat(courses.hasNext()).isFalse();
    }
}
