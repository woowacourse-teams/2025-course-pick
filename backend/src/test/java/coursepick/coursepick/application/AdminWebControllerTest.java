package coursepick.coursepick.application;

import coursepick.coursepick.domain.*;
import coursepick.coursepick.presentation.AdminWebController;
import coursepick.coursepick.presentation.dto.CourseRelaceWebRequest;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
class AdminWebControllerTest extends AbstractIntegrationTest {

    @Autowired
    AdminWebController sut;

    @Test
    void 코스_좌표_이름_길유형을_수정한다() {
        var course = new Course("한강 러닝 코스", RoadType.트랙, List.of(
                new Coordinate(37.5180, 127.0280, 0.0),
                new Coordinate(37.5175, 127.0270, 1000.0),
                new Coordinate(37.5170, 127.0265, 2.5),
                new Coordinate(37.5180, 127.0280, 0.0)
        ));
        Course savedCourse = dbUtil.saveCourse(course);

        List<List<Double>> newRawCoordinates = List.of(
                List.of(36.5180, 127.0280, 3.0),
                List.of(36.5175, 127.0270, 1000.0),
                List.of(36.5170, 127.0265, 3.0),
                List.of(36.5180, 127.0280, 0.0)
        );
        String newName = "newName";
        RoadType newRoadType = RoadType.트레일;

        sut.modifyCourse(savedCourse.id(), new CourseRelaceWebRequest(newRawCoordinates, newName, newRoadType));

        Course findCourse = dbUtil.findCourseById(savedCourse.id());
        List<Segment> segments = findCourse.segments();
        List<Coordinate> coordinates = CoordinateBuilder.fromSegments(segments).build();
        List<List<Double>> findRawCoordinates = coordinates.stream()
                .map(coordinate -> List.of(coordinate.latitude(), coordinate.longitude(), coordinate.elevation()))
                .toList();
        assertThat(findRawCoordinates).isEqualTo(newRawCoordinates);
        assertThat(findCourse.name().value()).isEqualTo(newName);
        assertThat(findCourse.roadType()).isEqualTo(newRoadType);
    }

    @Test
    void 코스_좌표만_수정한다() {
        var course = new Course("한강 러닝 코스", RoadType.트랙, List.of(
                new Coordinate(37.5180, 127.0280, 0.0),
                new Coordinate(37.5175, 127.0270, 1000.0),
                new Coordinate(37.5170, 127.0265, 2.5),
                new Coordinate(37.5180, 127.0280, 0.0)
        ));
        Course savedCourse = dbUtil.saveCourse(course);

        List<List<Double>> newRawCoordinates = List.of(
                List.of(36.5180, 127.0280, 3.0),
                List.of(36.5175, 127.0270, 1000.0),
                List.of(36.5170, 127.0265, 3.0),
                List.of(36.5180, 127.0280, 0.0)
        );

        sut.modifyCourse(savedCourse.id(), new CourseRelaceWebRequest(newRawCoordinates, null, null));

        Course findCourse = dbUtil.findCourseById(savedCourse.id());
        List<Segment> segments = findCourse.segments();
        List<Coordinate> coordinates = CoordinateBuilder.fromSegments(segments).build();
        List<List<Double>> findRawCoordinates = coordinates.stream()
                .map(coordinate -> List.of(coordinate.latitude(), coordinate.longitude(), coordinate.elevation()))
                .toList();
        assertThat(findRawCoordinates).isEqualTo(newRawCoordinates);
        assertThat(findCourse.name()).isEqualTo(savedCourse.name());
        assertThat(findCourse.roadType()).isEqualTo(savedCourse.roadType());
    }

    @Test
    void 코스_이름만_수정한다() {
        var course = new Course("한강 러닝 코스", RoadType.트랙, List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        Course savedCourse = dbUtil.saveCourse(course);

        String newName = "newName";

        sut.modifyCourse(savedCourse.id(), new CourseRelaceWebRequest(null, newName, null));

        Course findCourse = dbUtil.findCourseById(savedCourse.id());
        assertThat(findCourse.segments()).isEqualTo(savedCourse.segments());
        assertThat(findCourse.name().value()).isEqualTo(newName);
        assertThat(findCourse.roadType()).isEqualTo(savedCourse.roadType());
    }

    @Test
    void 코스_길유형만_수정한다() {
        var course = new Course("한강 러닝 코스", RoadType.트랙, List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        Course savedCourse = dbUtil.saveCourse(course);

        RoadType newRoadType = RoadType.트레일;

        sut.modifyCourse(savedCourse.id(), new CourseRelaceWebRequest(null, null, newRoadType));

        Course findCourse = dbUtil.findCourseById(savedCourse.id());
        assertThat(findCourse.segments()).isEqualTo(savedCourse.segments());
        assertThat(findCourse.name()).isEqualTo(savedCourse.name());
        assertThat(findCourse.roadType()).isEqualTo(newRoadType);
    }
}
