package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import coursepick.coursepick.test_util.CoordinateTestUtil;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseQueryServiceTest extends AbstractIntegrationTest {

    @Autowired
    CourseQueryService sut;

    @Test
    void 코스는_최소_1KM부터_탐색할_수_있다() {
        var course1 = new Course(null, new CourseName("한강 러닝 코스"), List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, new CourseName("양재천 산책길"), List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ), ADMIN_USER);
        var course3 = new Course(null, new CourseName("북악산 둘레길"), List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ), ADMIN_USER);
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
        var course1 = new Course(null, new CourseName("한강 러닝 코스"), List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, new CourseName("북악산 둘레길"), List.of(
                new Coordinate(38.602500, 126.967000),
                new Coordinate(38.603000, 126.968000),
                new Coordinate(38.603500, 126.969000),
                new Coordinate(38.602500, 126.967000)
        ), ADMIN_USER);
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
        var course1 = new Course(null, new CourseName("한강 러닝 코스"), List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, new CourseName("양재천 산책길"), List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ), ADMIN_USER);
        var course3 = new Course(null, new CourseName("북악산 둘레길"), List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ), ADMIN_USER);
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
        var course1 = new Course(null, new CourseName("한강 러닝 코스"), List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, new CourseName("양재천 산책길"), List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ), ADMIN_USER);
        var course3 = new Course(null, new CourseName("북악산 둘레길"), List.of(
                new Coordinate(37.602500, 126.967000),
                new Coordinate(37.603000, 126.968000),
                new Coordinate(37.603500, 126.969000),
                new Coordinate(37.602500, 126.967000)
        ), ADMIN_USER);
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
        for (int i = 0; i < 5; i++) courses.add(new Course(null, new CourseName("코스" + i), coordinates, ADMIN_USER));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 더_보여줄_코스가_있다() {
        List<Coordinate> coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 15; i++) courses.add(new Course(null, new CourseName("코스" + i), coordinates, ADMIN_USER));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void 다음_페이지의_코스를_찾는다() {
        List<Coordinate> coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 15; i++) courses.add(new Course(null, new CourseName("코스" + i), coordinates, ADMIN_USER));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 1);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isFalse();
        assertThat(result.courses().size()).isEqualTo(5);
    }

    @Test
    void 코스의_좌표_중에서_가장_가까운_좌표를_계산한다() {
        var course = new Course(null, new CourseName("한강 러닝 코스"), List.of(
                new Coordinate(0, 0),
                new Coordinate(0, 0.0001),
                new Coordinate(0.0001, 0.0001),
                new Coordinate(0.0001, 0),
                new Coordinate(0, 0)
        ), ADMIN_USER);
        var insertCourse = dbUtil.saveCourse(course);

        var result = sut.findClosestCoordinate(insertCourse.id(), 0.0002, 0.0002);

        assertThat(result).isEqualTo(new Coordinate(0.0001, 0.0001));
    }

    @Test
    void 코스가_존재하지_않을_경우_예외가_발생한다() {
        assertThatThrownBy(() -> sut.findClosestCoordinate("notId", 0, 0))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Nested
    class 나의_코스_조회 {

        private User user;
        private User otherUser;
        private List<Coordinate> sampleCoordinates;

        @BeforeEach
        void setUp() {
            user = dbUtil.saveUser(new User(UserProvider.KAKAO, "myProviderId"));
            otherUser = dbUtil.saveUser(new User(UserProvider.KAKAO, "otherProviderId"));
            sampleCoordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        }

        @Test
        void 내가_만든_코스만_조회된다() {
            dbUtil.saveCourse(new Course(null, new CourseName("내 코스1"), sampleCoordinates, user));
            dbUtil.saveCourse(new Course(null, new CourseName("내 코스2"), sampleCoordinates, user));
            dbUtil.saveCourse(new Course(null, new CourseName("남의 코스"), sampleCoordinates, otherUser));

            var result = sut.findCustomCourses(user.id(), null, null);

            assertThat(result.courses())
                    .hasSize(2)
                    .extracting(CourseResponse::name)
                    .containsExactlyInAnyOrder("내 코스1", "내 코스2");
        }

        @Test
        void 내가_만든_코스가_없으면_빈_리스트를_반환한다() {
            dbUtil.saveCourse(new Course(null, new CourseName("남의 코스"), sampleCoordinates, otherUser));

            var result = sut.findCustomCourses(user.id(), null, null);

            assertThat(result.courses()).isEmpty();
        }

        @Test
        void 최신순으로_정렬된다() {
            var now = LocalDateTime.now();
            var oldCourse = new Course(
                    null, new CourseName("오래된 코스"),
                    sampleCoordinates,
                    sampleCoordinates,
                    new Meter(1000),
                    List.of(),
                    user.id(),
                    new HashSet<>(),
                    now,
                    null
            );
            var newCourse = new Course(
                    null,
                    new CourseName("최신 코스"),
                    sampleCoordinates,
                    sampleCoordinates,
                    new Meter(1000),
                    List.of(),
                    user.id(),
                    new HashSet<>(),
                    now.plusSeconds(1L),
                    null
            );

            dbUtil.saveCourse(oldCourse);
            dbUtil.saveCourse(newCourse);

            var result = sut.findCustomCourses(user.id(), null, null);

            assertThat(result.courses())
                    .extracting(CourseResponse::name)
                    .containsExactly("최신 코스", "오래된 코스");
        }
    }
}
