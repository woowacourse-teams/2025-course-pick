package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.test_util.DatabaseCleaner;
import coursepick.coursepick.test_util.DatabaseInserter;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CourseApplicationServiceTest {

    @Autowired
    CourseApplicationService sut;

    @Autowired
    DatabaseInserter databaseInserter;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.deleteCourses();
    }

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

        List<CourseResponse> courses = sut.findNearbyCourses(latitude, longitude, null, null);

        assertThat(courses).hasSize(2)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name(), course2.name());
        assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
        assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
        assertThat(course3.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(1000.0);
    }

    @Test
    void 가까운_코스들을_조회하고_현위치에서_거리를_계산한다() {
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
        double mapLatitude = 37.5172;
        double mapLongitude = 127.0276;
        double userLatitude = 37.5153291;
        double userLongitude = 127.1031347;

        List<CourseResponse> courses = sut.findNearbyCourses(mapLatitude, mapLongitude, userLatitude, userLongitude);

        assertThat(course1.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isLessThan(1000.0);
        assertThat(course2.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isLessThan(1000.0);
        assertThat(course3.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isGreaterThan(1000.0);

        assertThat(courses).hasSize(2)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name(), course2.name());

        assertThat(courses).extracting(CourseResponse::distance).allMatch(Optional::isPresent);
        assertThat(courses.get(0).distance().get().value()).isCloseTo(6640, Percentage.withPercentage(1));
        assertThat(courses.get(1).distance().get().value()).isCloseTo(6583, Percentage.withPercentage(1));
    }

    @Test
    void 코스의_좌표_중에서_가장_가까운_좌표를_계산한다() {
        Course course = new Course("한강 러닝 코스", List.of(
                new Coordinate(0, 0),
                new Coordinate(10, 10),
                new Coordinate(0, 0)
        ));
        databaseInserter.saveCourse(course);
        Coordinate result = sut.findClosestCoordinate(course.id(), 5, 0);

        assertThat(result).isEqualTo(new Coordinate(2.5, 2.5));
    }

    @Test
    void 코스가_존재하지_않을_경우_예외가_발생한다() {
        Assertions.assertThatThrownBy(() -> sut.findClosestCoordinate(1L, 0, 0))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
