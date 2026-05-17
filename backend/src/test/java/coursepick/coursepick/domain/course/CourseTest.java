package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.exception.UnauthorizedException;
import coursepick.coursepick.domain.user.Nickname;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;
import static coursepick.coursepick.test_util.UserFixture.TEST_USER;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.data.Percentage.withPercentage;

class CourseTest {

    public static Stream<Arguments> targetAndDistance() {
        var base = new Coordinate(0, 0);

        return Stream.of(
                Arguments.of(upleft(base, 1000), 1000),
                Arguments.of(downleft(base, 2000), 2000 * ROOT2),
                Arguments.of(downright(base, 3000), 3000),
                Arguments.of(up(base, 3000), 0),
                Arguments.of(upright(base, 3000), 3000)

        );
    }

    public static Stream<Arguments> targetCoordinateAndExpectedCoordinate() {
        var base = new Coordinate(0, 0);
        return Stream.of(
                Arguments.of(upleft(base, 1000), up(base, 1000)),
                Arguments.of(up(right(base, 2000), 3000), up(base, 3000)),
                Arguments.of(downleft(base, 1000), base)
        );
    }

    @Test
    void 코스의_총_길이를_계산할_수_있다() {
        var course = new Course(null, new CourseName("코스"), square(new Coordinate(0, 0), 10000, 10000), ADMIN_USER);

        var distance = course.length();

        assertThat(distance.value()).isCloseTo(40000, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetAndDistance")
    void 특정_좌표에서_코스까지_가장_가까운_거리를_계산할_수_있다(Coordinate target, double expectedDistance) {
        var base = new Coordinate(0, 0);
        var course = new Course(null, new CourseName("코스"), square(base, 10000, 10000), ADMIN_USER);

        var distance = course.distanceFrom(target);

        assertThat(distance.value()).isCloseTo(expectedDistance, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetCoordinateAndExpectedCoordinate")
    void 코스에서_가장_가까운_좌표를_계산한다(Coordinate target, Coordinate expected) {
        var base = new Coordinate(0, 0);
        var course = new Course(null, new CourseName("코스"), square(base, 10000, 10000), ADMIN_USER);

        var minDistanceCoordinate = course.closestCoordinateFrom(target);

        assertThat(minDistanceCoordinate).isEqualTo(expected);
    }

    @Nested
    class 생성_테스트 {

        @Test
        void 코스를_생성한다() {
            assertThatCode(() -> new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER))
                    .doesNotThrowAnyException();
        }

        @Test
        void 유저가_만든_코스를_생성한다() {
            assertThatCode(() -> new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(2, 2)), TEST_USER))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1234567890123456789012345678901",
                "짧"
        })
        void 잘못된_길이의_이름으로_코스를_생성하면_예외가_발생한다(String name) {
            assertThatThrownBy(() -> new Course(null, new CourseName(name), of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER))
                    .isInstanceOf(IllegalArgumentException.class);
        }


        @Test
        void 코스의_좌표의_개수가_2보다_적으면_예외가_발생한다() {
            assertThatThrownBy(() -> new Course(null, new CourseName("코스"), of(new Coordinate(0, 0)), ADMIN_USER))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 코스_생성시_첫_좌표_끝_좌표만_존재할때_둘은_중복될_수_없다() {
            assertThatThrownBy(() -> new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(0, 0)), ADMIN_USER))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void 동일_유저_신고시_예외를_던진다() {

        Course course = new Course(null, new CourseName("코스"), List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("user1", UserProvider.KAKAO, "providerId", Nickname.random());

        course.addReport(user1);

        assertThatThrownBy(() -> course.addReport(user1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 신규_코스의_태그는_빈_리스트다() {
        var course = new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER);

        assertThat(course.tags()).isEmpty();
    }

    @Test
    void 태그를_갱신할_수_있다() {
        var course = new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER);

        course.updateTags(List.of(CourseTag.NIGHT_VIEW, CourseTag.FLAT));

        assertThat(course.tags()).containsExactly(CourseTag.NIGHT_VIEW, CourseTag.FLAT);
    }

    @Test
    void 태그_갱신시_5개를_초과하면_5개로_제한된다() {
        var course = new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER);

        course.updateTags(List.of(
                CourseTag.NIGHT_VIEW, CourseTag.FLAT, CourseTag.RIVERSIDE,
                CourseTag.PARK, CourseTag.QUIET, CourseTag.SCENIC, CourseTag.SHADY
        ));

        assertThat(course.tags()).hasSize(CourseTag.MAX_TAGS_PER_COURSE);
    }

    @Test
    void 태그_갱신시_중복은_제거된다() {
        var course = new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER);

        course.updateTags(List.of(CourseTag.NIGHT_VIEW, CourseTag.NIGHT_VIEW, CourseTag.FLAT));

        assertThat(course.tags()).containsExactly(CourseTag.NIGHT_VIEW, CourseTag.FLAT);
    }

    @Test
    void 다른_유저가_리뷰를_삭제하면_예외가_발생한다() {
        var course = new Course(null, new CourseName("코스"), of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER);
        course.reviews().add(new Review(TEST_USER, "content", 5));
        Review review = course.reviews().getFirst();
        User otherUser = new User(UserProvider.KAKAO, "otherProviderId");

        assertThatThrownBy(() -> course.verifyRemovableReview(review, otherUser.id()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 한_유저가_같은_코스에_두_번_리뷰를_남기면_예외가_발생한다() {
        Course course = new Course(null, new CourseName("코스"), List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("user1", UserProvider.KAKAO, "providerId", Nickname.random());
        course.reviews().add(new Review(user1, "content", 5));

        assertThatThrownBy(() -> course.verifyWriteReviewEligibility(user1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 해당 코스에 리뷰를 작성했습니다");
    }
}
