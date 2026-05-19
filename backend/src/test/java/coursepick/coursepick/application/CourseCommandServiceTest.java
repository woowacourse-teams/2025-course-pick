package coursepick.coursepick.application;

import coursepick.coursepick.application.exception.UnauthorizedException;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.user.Nickname;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;

import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CourseCommandServiceTest extends AbstractIntegrationTest {

    @Autowired
    CourseCommandService sut;

    @MockitoBean
    Alerter courseAlerter;

    @MockitoBean
    CourseTagGenerator courseTagGenerator;

    @Test
    void 동일한_유저는_코스_횟수에_카운트_하지_않는다() {
        User user = new User("507f1f77bcf86cd799439011", UserProvider.KAKAO, "provierId", Nickname.random());

        var course1 = new Course(null, new CourseName("한강 러닝 코스"), List.of(
                new Coordinate(37.5180, 127.0280),
                new Coordinate(37.5175, 127.0270),
                new Coordinate(37.5170, 127.0265),
                new Coordinate(37.5180, 127.0280)
        ), user);

        dbUtil.saveUser(user);
        Course course = dbUtil.saveCourse(course1);

        sut.reportCourse(course.id(), user.id());

        Course result = dbUtil.findCourseById(course.id());
        assertThat(result.reportUserIds()).hasSize(1);
    }

    @Test
    void 두번_이하로_신고되면_알람이_안간다() {
        Course course = new Course("507f1f77bcf86cd799439011", new CourseName("코스"), List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("507f191e810c19729de860ea", UserProvider.KAKAO, "providerId1", Nickname.random());
        User user2 = new User("507f191e810c19729de860eb", UserProvider.KAKAO, "providerId2", Nickname.random());


        Course targetCourse = dbUtil.saveCourse(course);
        dbUtil.saveUser(user1);
        dbUtil.saveUser(user2);

        sut.reportCourse(targetCourse.id(), user1.id());
        sut.reportCourse(targetCourse.id(), user2.id());

        Course result = dbUtil.findCourseById(targetCourse.id());
        assertThat(result.reportUserIds()).hasSize(2);
        verify(courseAlerter, times(0)).alertCourse(any(Course.class));
    }

    @Test
    void 세번_이상으로_신고되면_알람이_간다() {
        Course course = new Course(null, new CourseName("코스"), List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("507f191e810c19729de860ea", UserProvider.KAKAO, "providerId1", Nickname.random());
        User user2 = new User("507f191e810c19729de860eb", UserProvider.KAKAO, "providerId2", Nickname.random());
        User user3 = new User("507f191e810c19729de860ec", UserProvider.KAKAO, "providerId3", Nickname.random());


        Course targetCourse = dbUtil.saveCourse(course);
        dbUtil.saveUser(user1);
        dbUtil.saveUser(user2);
        dbUtil.saveUser(user3);

        sut.reportCourse(targetCourse.id(), user1.id());
        sut.reportCourse(targetCourse.id(), user2.id());
        sut.reportCourse(targetCourse.id(), user3.id());

        Course result = dbUtil.findCourseById(targetCourse.id());
        assertThat(result.reportUserIds()).hasSize(3);
        verify(courseAlerter, times(1)).alertCourse(any());
    }

    @Nested
    class 태그_재생성 {

        @Test
        void 리뷰가_있으면_태그를_생성하여_코스에_저장한다() {
            var course = new Course(null, new CourseName("코스"), List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
            course.reviews().add(new Review(ADMIN_USER, "야경이 멋집니다", 5));
            Course saved = dbUtil.saveCourse(course);

            org.mockito.Mockito.when(courseTagGenerator.generate(any(Course.class)))
                    .thenReturn(List.of(CourseTag.NIGHT_VIEW, CourseTag.FLAT));

            sut.regenerateTags(saved.id());

            Course result = dbUtil.findCourseById(saved.id());
            assertThat(result.tags()).containsExactly(CourseTag.NIGHT_VIEW, CourseTag.FLAT);
            verify(courseTagGenerator, times(1)).generate(any(Course.class));
        }

        @Test
        void 리뷰가_없으면_태그를_생성하지_않는다() {
            var course = new Course(null, new CourseName("코스"), List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
            Course saved = dbUtil.saveCourse(course);

            sut.regenerateTags(saved.id());

            Course result = dbUtil.findCourseById(saved.id());
            assertThat(result.tags()).isEmpty();
            verify(courseTagGenerator, times(0)).generate(any(Course.class));
        }
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
                    .isInstanceOf(UnauthorizedException.class);

        }

        @ParameterizedTest
        @ValueSource(strings = {
                "나만의 코스",
                " 나만의 코스   "
        })
        void 앞_뒤_공백을_제거하여_코스를_생성한다(String name) {
            String expectedName = "나만의 코스";
            List<Coordinate> coordinates = List.of(
                    new Coordinate(37.602500, 126.967000),
                    new Coordinate(37.603000, 126.968000),
                    new Coordinate(37.603500, 126.969000),
                    new Coordinate(37.602500, 126.967000)
            );

            sut.addCustomCourse(name, coordinates, user.id());

            Course result = dbUtil.findCourseByName(expectedName);
            assertThat(result.name().value()).isEqualTo(expectedName);
        }

    }

    @Nested
    class 리뷰_추가 {

        private User reviewer;
        private String courseId;

        @BeforeEach
        void setUp() {
            User courseCreator = dbUtil.saveUser(new User(UserProvider.KAKAO, "creatorProviderId"));
            reviewer = dbUtil.saveUser(new User(UserProvider.KAKAO, "reviewerProviderId"));

            Course course = new Course(null, new CourseName("테스트 코스"), List.of(
                    new Coordinate(37.5180, 127.0280),
                    new Coordinate(37.5175, 127.0270),
                    new Coordinate(37.5170, 127.0265),
                    new Coordinate(37.5180, 127.0280)
            ), courseCreator);

            courseId = dbUtil.saveCourse(course).id();
        }

        @Test
        void 리뷰를_추가하면_DB에_저장된다() {
            sut.addReview(courseId, reviewer.id(), "좋은 코스입니다", 4);

            Course result = dbUtil.findCourseById(courseId);
            assertThat(result.reviews()).hasSize(1);
            assertThat(result.reviews().get(0).userId()).isEqualTo(reviewer.id());
            assertThat(result.reviews().get(0).content()).isEqualTo("좋은 코스입니다");
            assertThat(result.reviews().get(0).rating()).isEqualTo(4);
        }

        @Test
        void 존재하지_않는_코스에_리뷰를_추가하면_예외가_발생한다() {
            assertThatThrownBy(() -> sut.addReview("notExistCourseId", reviewer.id(), "좋은 코스입니다", 4))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 이미_리뷰를_작성한_유저가_추가하면_예외가_발생한다() {
            sut.addReview(courseId, reviewer.id(), "좋은 코스입니다", 4);

            assertThatThrownBy(() -> sut.addReview(courseId, reviewer.id(), "또 쓰는 리뷰", 3))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 리뷰_삭제 {

        private User reviewer;
        private User otherUser;
        private String courseId;
        private String reviewId;

        @BeforeEach
        void setUp() {
            User courseCreator = dbUtil.saveUser(new User(UserProvider.KAKAO, "creatorProviderId"));
            reviewer = dbUtil.saveUser(new User(UserProvider.KAKAO, "reviewerProviderId"));
            otherUser = dbUtil.saveUser(new User(UserProvider.KAKAO, "otherProviderId"));

            Course course = new Course(null, new CourseName("테스트 코스"), List.of(
                    new Coordinate(37.5180, 127.0280),
                    new Coordinate(37.5175, 127.0270),
                    new Coordinate(37.5170, 127.0265),
                    new Coordinate(37.5180, 127.0280)
            ), courseCreator);

            courseId = dbUtil.saveCourse(course).id();
            sut.addReview(courseId, reviewer.id(), "좋은 코스입니다", 5);
            reviewId = dbUtil.findCourseById(courseId).reviews().getFirst().id();
        }

        @Test
        void 리뷰를_삭제하면_DB에서_제거된다() {
            sut.deleteReview(courseId, reviewId, reviewer.id());

            Course result = dbUtil.findCourseById(courseId);
            assertThat(result.reviews()).isEmpty();
        }

        @Test
        void 존재하지_않는_코스의_리뷰를_삭제하면_예외가_발생한다() {
            assertThatThrownBy(() -> sut.deleteReview("notExistCourseId", reviewId, reviewer.id()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 존재하지_않는_리뷰를_삭제하면_예외가_발생한다() {
            assertThatThrownBy(() -> sut.deleteReview(courseId, "notExistReviewId", reviewer.id()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 작성자가_아닌_경우_삭제하면_예외가_발생한다() {
            assertThatThrownBy(() -> sut.deleteReview(courseId, reviewId, otherUser.id()))
                    .isInstanceOf(UnauthorizedException.class);
        }
    }

    @Nested
    class 리뷰_신고 {

        private User reporter;
        private String courseId;
        private String reviewId;

        @BeforeEach
        void setUp() {
            User courseCreator = dbUtil.saveUser(new User(UserProvider.KAKAO, "creatorProviderId"));
            reporter = dbUtil.saveUser(new User(UserProvider.KAKAO, "reporterProviderId"));

            Course course = new Course(null, new CourseName("테스트 코스3"), List.of(
                    new Coordinate(37.5180, 127.0280),
                    new Coordinate(37.5175, 127.0270),
                    new Coordinate(37.5170, 127.0265),
                    new Coordinate(37.5180, 127.0280)
            ), courseCreator);

            courseId = dbUtil.saveCourse(course).id();

            sut.addReview(courseId, courseCreator.id(), "좋은 코스입니다", 5);

            reviewId = dbUtil.findCourseById(courseId).reviews().get(0).id();
        }

        @Test
        void 리뷰를_신고하면_알람이_간다() {
            sut.reportReview(courseId, reviewId, reporter.id());

            verify(courseAlerter, times(0)).alertCourse(any());
        }

        @Test
        void 리뷰를_두번_신고하면_알람이_두번_간다() {
            User reporter2 = dbUtil.saveUser(new User(UserProvider.KAKAO, "reporter2ProviderId"));

            sut.reportReview(courseId, reviewId, reporter.id());
            sut.reportReview(courseId, reviewId, reporter2.id());

            verify(courseAlerter, times(0)).alertCourse(any());
        }
    }
}
