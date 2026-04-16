package coursepick.coursepick.domain.course;

import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
import static coursepick.coursepick.test_util.UserFixture.ADMIN_USER;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.Mockito.*;

class CourseTest {

    private static final User TEST_USER = new User("userId", UserProvider.KAKAO, "providerId");

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
        var course = new Course(null, "코스", square(new Coordinate(0, 0), 10000, 10000), ADMIN_USER);

        var distance = course.length();

        assertThat(distance.value()).isCloseTo(40000, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetAndDistance")
    void 특정_좌표에서_코스까지_가장_가까운_거리를_계산할_수_있다(Coordinate target, double expectedDistance) {
        var base = new Coordinate(0, 0);
        var course = new Course(null, "코스", square(base, 10000, 10000), ADMIN_USER);

        var distance = course.distanceFrom(target);

        assertThat(distance.value()).isCloseTo(expectedDistance, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetCoordinateAndExpectedCoordinate")
    void 코스에서_가장_가까운_좌표를_계산한다(Coordinate target, Coordinate expected) {
        var base = new Coordinate(0, 0);
        var course = new Course(null, "코스", square(base, 10000, 10000), ADMIN_USER);

        var minDistanceCoordinate = course.closestCoordinateFrom(target);

        assertThat(minDistanceCoordinate).isEqualTo(expected);
    }

    @Nested
    class 생성_테스트 {

        @Test
        void 코스를_생성한다() {
            assertThatCode(() -> new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER))
                    .doesNotThrowAnyException();
        }

        @Test
        void 유저가_만든_코스를_생성한다() {
            assertThatCode(() -> new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(2, 2)), TEST_USER))
                    .doesNotThrowAnyException();
        }

        @Test
        void 앞_뒤_공백을_제거하여_생성한다() {
            var course = new Course(null, " 코스   ", List.of(new Coordinate(0, 0), new Coordinate(2, 2)), TEST_USER);
            assertThat(course.name().value()).isEqualTo("코스");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1234567890123456789012345678901",
                "짧"
        })
        void 잘못된_길이의_이름으로_코스를_생성하면_예외가_발생한다(String name) {
            assertThatThrownBy(() -> new Course(null, name, List.of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "코스 이름",
                "코스  이름",
        })
        void 이름의_연속공백을_한_칸으로_변환하여_코스를_생성한다(String name) {
            var course = new Course(null, name, List.of(new Coordinate(0, 0), new Coordinate(2, 2)), ADMIN_USER);
            assertThat(course.name().value()).isEqualTo("코스 이름");
        }

        @Test
        void 코스의_좌표의_개수가_2보다_적으면_예외가_발생한다() {
            assertThatThrownBy(() -> new Course(null, "코스", List.of(new Coordinate(0, 0)), ADMIN_USER))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 코스_생성시_첫_좌표_끝_좌표만_존재할때_둘은_중복될_수_없다() {
            assertThatThrownBy(() -> new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(0, 0)), ADMIN_USER))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void 두번_이하로_신고되면_알람이_안간다() {
        Alerter alerter = mock(Alerter.class);
        Course course = new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("user1", UserProvider.KAKAO, "providerId");
        User user2 = new User("user2", UserProvider.KAKAO, "providerId");

        course.report(user1, alerter, "test");
        course.report(user2, alerter, "test");

        verify(alerter, times(0)).alert(Mockito.anyString());
    }

    @Test
    void 세번_이상으로_신고되면_알람이_간다() {
        Alerter alerter = mock(Alerter.class);

        Course course = new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("user1", UserProvider.KAKAO, "providerId");
        User user2 = new User("user2", UserProvider.KAKAO, "providerId");
        User user3 = new User("user3", UserProvider.KAKAO, "providerId");

        course.report(user1, alerter, "test");
        course.report(user2, alerter, "test");
        course.report(user3, alerter, "test");

        verify(alerter, times(1)).alert(Mockito.anyString());
    }

    @Test
    void 동일_유저는_카운트하지_않는다() {
        Alerter alerter = mock(Alerter.class);

        Course course = new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(10, 10)), ADMIN_USER);
        User user1 = new User("user1", UserProvider.KAKAO, "providerId");
        User user2 = new User("user2", UserProvider.KAKAO, "providerId");

        course.report(user1, alerter, "test");
        course.report(user1, alerter, "test");
        course.report(user1, alerter, "test");
        course.report(user1, alerter, "test");
        course.report(user2, alerter, "test");

        verify(alerter, times(0)).alert(Mockito.anyString());
    }
}
