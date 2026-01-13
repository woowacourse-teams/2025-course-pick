package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.dto.SnapResponse;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseFindCondition;
import coursepick.coursepick.domain.course.UserCreatedCourse;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import coursepick.coursepick.test_util.CoordinateTestUtil;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseApplicationServiceTest extends AbstractIntegrationTest {

    @Autowired
    CourseApplicationService sut;

    @Test
    void 코스는_최소_1KM부터_탐색할_수_있다() {
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        var course2 = new Course(null, "양재천 산책길", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ));
        var course3 = new Course(null, "북악산 둘레길", List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ));
        dbUtil.saveCourse(course1);
        dbUtil.saveCourse(course2);
        dbUtil.saveCourse(course3);

        var latitude = 37.5122;
        var longitude = 127.0276;
        var condition = new CourseFindCondition(latitude, longitude, 300, null, null, null);

        var nearbyCourses = sut.findNearbyCourses(condition, null, null);

        assertThat(nearbyCourses.courses()).hasSize(2)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name().value(), course2.name().value());
        assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(300);
        assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(300);
        assertThat(course3.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(1000.0);
    }

    @Test
    void 코스는_최대_3KM까지_탐색할_수_있다() {
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        var course2 = new Course(null, "북악산 둘레길", List.of(
                new Coordinate(38.602500, 126.967000),
                new Coordinate(38.603000, 126.968000),
                new Coordinate(38.603500, 126.969000),
                new Coordinate(38.602500, 126.967000)
        ));
        dbUtil.saveCourse(course1);
        dbUtil.saveCourse(course2);

        var latitude = 37.5122;
        var longitude = 127.0276;
        var condition = new CourseFindCondition(latitude, longitude, 15000, null, null, null);

        var nearbyCourses = sut.findNearbyCourses(condition, null, null);

        assertThat(nearbyCourses.courses()).hasSize(1)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name().value());
        assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(3000);
        assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(3000);
    }

    @Test
    void 가까운_코스들을_조회한다() {
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        var course2 = new Course(null, "양재천 산책길", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ));
        var course3 = new Course(null, "북악산 둘레길", List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ));
        dbUtil.saveCourse(course1);
        dbUtil.saveCourse(course2);
        dbUtil.saveCourse(course3);

        var latitude = 37.5172;
        var longitude = 127.0276;
        var condition = new CourseFindCondition(latitude, longitude, 1000, null, null, null);

        var courses = sut.findNearbyCourses(condition, null, null);

        assertThat(courses.courses()).hasSize(2)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name().value(), course2.name().value());
        assertThat(course1.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
        assertThat(course2.distanceFrom(new Coordinate(latitude, longitude)).value()).isLessThan(1000.0);
        assertThat(course3.distanceFrom(new Coordinate(latitude, longitude)).value()).isGreaterThan(1000.0);
    }

    @Test
    void 가까운_코스들을_조회하고_현위치에서_거리를_계산한다() {
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ));
        var course2 = new Course(null, "양재천 산책길", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ));
        var course3 = new Course(null, "북악산 둘레길", List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ));
        dbUtil.saveCourse(course1);
        dbUtil.saveCourse(course2);
        dbUtil.saveCourse(course3);
        var mapLatitude = 37.5172;
        var mapLongitude = 127.0276;
        var userLatitude = 37.5153291;
        var userLongitude = 127.1031347;
        var condition = new CourseFindCondition(mapLatitude, mapLongitude, 1000, null, null, null);

        var courses = sut.findNearbyCourses(condition, userLatitude, userLongitude);

        assertThat(course1.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isLessThan(1000.0);
        assertThat(course2.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isLessThan(1000.0);
        assertThat(course3.distanceFrom(new Coordinate(mapLatitude, mapLongitude)).value()).isGreaterThan(1000.0);

        assertThat(courses.courses()).hasSize(2)
                .extracting(CourseResponse::name)
                .containsExactlyInAnyOrder(course1.name().value(), course2.name().value());

        assertThat(courses.courses()).extracting(CourseResponse::distance).allMatch(Optional::isPresent);
        assertThat(courses.courses().get(0).distance().get().value()).isCloseTo(6640, Percentage.withPercentage(1));
        assertThat(courses.courses().get(1).distance().get().value()).isCloseTo(6583, Percentage.withPercentage(1));
    }

    @Test
    void 더_보여줄_코스가_없다() {
        List<Coordinate> coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 5; i++) courses.add(new Course(null, "코스" + i, coordinates));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 더_보여줄_코스가_있다() {
        List<Coordinate> coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 15; i++) courses.add(new Course(null, "코스" + i, coordinates));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void 다음_페이지의_코스를_찾는다() {
        List<Coordinate> coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 15; i++) courses.add(new Course(null, "코스" + i, coordinates));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 1);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isFalse();
        assertThat(result.courses().size()).isEqualTo(5);
    }

    @Test
    void 코스의_좌표_중에서_가장_가까운_좌표를_계산한다() {
        var course = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(0, 0),
                new Coordinate(0, 0.0001),
                new Coordinate(0.0001, 0.0001),
                new Coordinate(0.0001, 0),
                new Coordinate(0, 0)
        ));
        var insertCourse = dbUtil.saveCourse(course);

        var result = sut.findClosestCoordinate(insertCourse.id(), 0.0002, 0.0002);

        assertThat(result).isEqualTo(new Coordinate(0.0001, 0.0001));
    }

    @Test
    void 코스가_존재하지_않을_경우_예외가_발생한다() {
        assertThatThrownBy(() -> sut.findClosestCoordinate("notId", 0, 0))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 두개의_좌표에_대해서_도로에_스냅할_수_있다() {
        mock(osrmSnapResponse());
        var coordinates = List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.6180, 127.1280)
        );

        SnapResponse snapResponse = sut.snapCoordinates(coordinates);

        assertThat(snapResponse.coordinates()).hasSize(2);
        assertThat(snapResponse.length()).isEqualTo(100);
    }

    private static String osrmSnapResponse() {
        return """
                {
                  "code": "Ok",
                  "matchings": [
                    {
                      "geometry": {
                        "coordinates": [
                          [127.028, 37.518],
                          [127.128, 37.618]
                        ],
                        "type": "LineString"
                      },
                      "distance": 100,
                      "confidence": 0.85
                    }
                  ]
                }
                """;
    }

    @Test
    void 유저는_코스를_추가할_수_있다() {
        User user = new User(UserProvider.KAKAO, "testProviderId123");
        User savedUser = dbUtil.saveUser(user);

        List<Coordinate> coordinates = List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        );

        CourseResponse result = sut.create(savedUser.id(), "유저코스테스트", coordinates);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("유저코스테스트");

        Course savedCourse = dbUtil.findCourseById(result.id());
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.name().value()).isEqualTo("유저코스테스트");

        // Then: UserCreatedCourse 관계가 생성되었는지 확인
        UserCreatedCourse userCreatedCourse = dbUtil.findUserCourse(savedUser.id(), savedCourse.id());
        assertThat(userCreatedCourse).isNotNull();
    }

    @Test
    void 존재하지_않는_사용자는_코스를_추가할_수_없다() {
        List<Coordinate> coordinates = List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        );

        assertThatThrownBy(() -> sut.create("nonexistent-user-id", "코스", coordinates))
                .isInstanceOf(NoSuchElementException.class);
    }
}
