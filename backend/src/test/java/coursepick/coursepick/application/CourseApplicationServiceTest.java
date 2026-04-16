package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseFindCondition;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.infrastructure.discord.DiscordAlerter;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import coursepick.coursepick.test_util.CoordinateTestUtil;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CourseApplicationServiceTest extends AbstractIntegrationTest {

    @Autowired
    CourseApplicationService sut;

    @MockitoBean
    DiscordAlerter discordAlerter;

    @Test
    void 코스는_최소_1KM부터_탐색할_수_있다() {
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, "양재천 산책길", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ), ADMIN_USER);
        var course3 = new Course(null, "북악산 둘레길", List.of(
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
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, "북악산 둘레길", List.of(
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
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, "양재천 산책길", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ), ADMIN_USER);
        var course3 = new Course(null, "북악산 둘레길", List.of(
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
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), ADMIN_USER);
        var course2 = new Course(null, "양재천 산책길", List.of(
                new Coordinate(37.5165, 127.0285),
                new Coordinate(37.5160, 127.0278),
                new Coordinate(37.5155, 127.0265),
                new Coordinate(37.5165, 127.0285)
        ), ADMIN_USER);
        var course3 = new Course(null, "북악산 둘레길", List.of(
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
        for (int i = 0; i < 5; i++) courses.add(new Course(null, "코스" + i, coordinates, ADMIN_USER));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 더_보여줄_코스가_있다() {
        List<Coordinate> coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 15; i++) courses.add(new Course(null, "코스" + i, coordinates, ADMIN_USER));
        dbUtil.saveAllCourses(courses);
        var condition = new CourseFindCondition(37.5175, 127.0270, 3000, null, null, 0);

        var result = sut.findNearbyCourses(condition, null, null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void 다음_페이지의_코스를_찾는다() {
        List<Coordinate> coordinates = CoordinateTestUtil.square(new Coordinate(37.5180, 127.0280), new Coordinate(37.5175, 127.0270));
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 15; i++) courses.add(new Course(null, "코스" + i, coordinates, ADMIN_USER));
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

    @Test
    void 동일한_유저는_코스_횟수에_카운트_하지_않는다() {
        User user = new User("user1", UserProvider.KAKAO, "provierId");
        var course1 = new Course(null, "한강 러닝 코스", List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), user);

        dbUtil.saveUser(user);
        Course course = dbUtil.saveCourse(course1);

        sut.report(course.id(), user.id());

        Course result = dbUtil.findCourseById(course.id());
        assertThat(result.reportUserIds()).hasSize(1);
    }

    @Test
    void 두번_이하로_신고되면_알람이_안간다() {
        DiscordAlerter alerter = mock(DiscordAlerter.class);
        Course course = new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("user1", UserProvider.KAKAO, "providerId1");
        User user2 = new User("user2", UserProvider.KAKAO, "providerId2");

        Course targetCourse = dbUtil.saveCourse(course);
        dbUtil.saveUser(user1);
        dbUtil.saveUser(user2);

        sut.report(targetCourse.id(), user1.id());
        sut.report(targetCourse.id(), user2.id());

        Course result = dbUtil.findCourseById(targetCourse.id());
        assertThat(result.reportUserIds()).hasSize(2);
        verify(alerter, times(0)).alert(any(Course.class));
    }

    @Test
    void 세번_이상으로_신고되면_알람이_간다() {
        DiscordAlerter alerter = mock(DiscordAlerter.class);

        Course course = new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("user1", UserProvider.KAKAO, "providerId1");
        User user2 = new User("user2", UserProvider.KAKAO, "providerId2");
        User user3 = new User("user3", UserProvider.KAKAO, "providerId3");

        Course targetCourse = dbUtil.saveCourse(course);
        dbUtil.saveUser(user1);
        dbUtil.saveUser(user2);
        dbUtil.saveUser(user3);

        sut.report(targetCourse.id(), user1.id());
        sut.report(targetCourse.id(), user2.id());
        sut.report(targetCourse.id(), user3.id());

        Course result = dbUtil.findCourseById(targetCourse.id());
        assertThat(result.reportUserIds()).hasSize(3);
        verify(alerter, times(1)).alert(any(Course.class));
    }

    @Nested
    class 유저_코스_생성 {

        private User user;

        @BeforeEach
        void setUp() {
            user = dbUtil.saveUser(new User(UserProvider.KAKAO, "providerId"));
        }

        @Test
        void 유저가_생성한_코스를_저장한다() {

            String name = "나만의 코스1";
            List<Coordinate> coordinates = List.of(
                    new Coordinate(37.602500, 126.967000),
                    new Coordinate(37.603000, 126.968000),
                    new Coordinate(37.603500, 126.969000),
                    new Coordinate(37.602500, 126.967000)
            );

            sut.addCustomCourse(name, coordinates, user.id());

            Course result = dbUtil.findCourseByName(name);
            assertThat(result.name().value()).isEqualTo(name);
        }


        @Test
        void 유저가_존재하지_않을_경우_예외를_던진다() {
            String name = "나만의 코스1";
            List<Coordinate> coordinates = List.of(
                    new Coordinate(37.602500, 126.967000),
                    new Coordinate(37.603000, 126.968000),
                    new Coordinate(37.603500, 126.969000),
                    new Coordinate(37.602500, 126.967000)
            );

            assertThatThrownBy(() -> sut.addCustomCourse(name, coordinates, "userid"))
                    .isInstanceOf(NoSuchElementException.class);

        }
    }
}
