package coursepick.coursepick.domain.course;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
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
        var course = new Course(null, "코스", square(new Coordinate(0, 0), 10000, 10000));

        var distance = course.length();

        assertThat(distance.value()).isCloseTo(40000, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetAndDistance")
    void 특정_좌표에서_코스까지_가장_가까운_거리를_계산할_수_있다(Coordinate target, double expectedDistance) {
        var base = new Coordinate(0, 0);
        var course = new Course(null, "코스", square(base, 10000, 10000));

        var distance = course.distanceFrom(target);

        assertThat(distance.value()).isCloseTo(expectedDistance, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetCoordinateAndExpectedCoordinate")
    void 코스에서_가장_가까운_좌표를_계산한다(Coordinate target, Coordinate expected) {
        var base = new Coordinate(0, 0);
        var course = new Course(null, "코스", square(base, 10000, 10000));

        var minDistanceCoordinate = course.closestCoordinateFrom(target);

        assertThat(minDistanceCoordinate).isEqualTo(expected);
    }

    @Nested
    class 생성_테스트 {

        @Test
        void 코스를_생성한다() {
            assertThatCode(() -> new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(2, 2))))
                    .doesNotThrowAnyException();
        }

        @Test
        void 앞_뒤_공백을_제거하여_생성한다() {
            var course = new Course(null, " 코스   ", List.of(new Coordinate(0, 0), new Coordinate(2, 2)));
            assertThat(course.name().value()).isEqualTo("코스");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1234567890123456789012345678901",
                "짧"
        })
        void 잘못된_길이의_이름으로_코스를_생성하면_예외가_발생한다(String name) {
            assertThatThrownBy(() -> new Course(null, name, List.of(new Coordinate(0, 0), new Coordinate(2, 2))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "코스 이름",
                "코스  이름",
        })
        void 이름의_연속공백을_한_칸으로_변환하여_코스를_생성한다(String name) {
            var course = new Course(null, name, List.of(new Coordinate(0, 0), new Coordinate(2, 2)));
            assertThat(course.name().value()).isEqualTo("코스 이름");
        }

        @Test
        void 코스의_좌표의_개수가_2보다_적으면_예외가_발생한다() {
            assertThatThrownBy(() -> new Course(null, "코스", List.of(new Coordinate(0, 0))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 코스_생성시_첫_좌표_끝_좌표만_존재할때_둘은_중복될_수_없다() {
            assertThatThrownBy(() -> new Course(null, "코스", List.of(new Coordinate(0, 0), new Coordinate(0, 0))))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
