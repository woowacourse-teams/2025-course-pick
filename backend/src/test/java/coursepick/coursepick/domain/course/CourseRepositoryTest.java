package coursepick.coursepick.domain.course;

import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

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
}
