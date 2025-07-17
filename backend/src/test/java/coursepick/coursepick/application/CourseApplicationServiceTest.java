package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.DatabaseInserter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CourseApplicationServiceTest {

    @Autowired
    CourseApplicationService sut;

    @Autowired
    DatabaseInserter databaseInserter;

    @Test
    void 가까운_코스들을_조회한다() {
        Course course1 = new Course("한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        Course course2 = new Course("양재천 산책길", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ));
        Course course3 = new Course("북악산 둘레길", List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ));
        databaseInserter.saveCourse(course1);
        databaseInserter.saveCourse(course2);
        databaseInserter.saveCourse(course3);
        double latitude = 37.5172;
        double longitude = 127.0276;

        List<CourseResponse> courses = sut.findNearbyCourses(latitude, longitude);

        assertThat(courses).hasSize(2)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name(), course2.name());
        assertThat(course1.minDistanceFrom(new Coordinate(latitude, longitude))).isLessThan(1000.0);
        assertThat(course2.minDistanceFrom(new Coordinate(latitude, longitude))).isLessThan(1000.0);
        assertThat(course3.minDistanceFrom(new Coordinate(latitude, longitude))).isGreaterThan(1000.0);
    }
}
