package coursepick.coursepick.domain;

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
        var course = new Course("코스", square(new Coordinate(0, 0), 10000, 10000));

        var distance = course.length();

        assertThat(distance.value()).isCloseTo(40000, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetAndDistance")
    void 특정_좌표에서_코스까지_가장_가까운_거리를_계산할_수_있다(Coordinate target, double expectedDistance) {
        var base = new Coordinate(0, 0);
        var course = new Course("코스", square(base, 10000, 10000));

        var distance = course.distanceFrom(target);

        assertThat(distance.value()).isCloseTo(expectedDistance, withPercentage(1));
    }

    @ParameterizedTest
    @MethodSource("targetCoordinateAndExpectedCoordinate")
    void 코스에서_가장_가까운_좌표를_계산한다(Coordinate target, Coordinate expected) {
        var base = new Coordinate(0, 0);
        var course = new Course("코스", square(base, 10000, 10000));

        var minDistanceCoordinate = course.closestCoordinateFrom(target);

        assertThat(minDistanceCoordinate).isEqualTo(expected);
    }

    @Nested
    class 생성_테스트 {

        @Test
        void 코스를_생성한다() {
            assertThatCode(() -> new Course("코스", List.of(new Coordinate(0, 0), new Coordinate(2, 2))))
                    .doesNotThrowAnyException();
        }

        @Test
        void 앞_뒤_공백을_제거하여_생성한다() {
            var course = new Course(" 코스   ", List.of(new Coordinate(0, 0), new Coordinate(2, 2)));
            assertThat(course.name().value()).isEqualTo("코스");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1234567890123456789012345678901",
                "짧"
        })
        void 잘못된_길이의_이름으로_코스를_생성하면_예외가_발생한다(String name) {
            assertThatThrownBy(() -> new Course(name, List.of(new Coordinate(0, 0), new Coordinate(2, 2))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "코스 이름",
                "코스  이름",
        })
        void 이름의_연속공백을_한_칸으로_변환하여_코스를_생성한다(String name) {
            var course = new Course(name, List.of(new Coordinate(0, 0), new Coordinate(2, 2)));
            assertThat(course.name().value()).isEqualTo("코스 이름");
        }

        @Test
        void 코스의_좌표의_개수가_2보다_적으면_예외가_발생한다() {
            assertThatThrownBy(() -> new Course("코스", List.of(new Coordinate(0, 0))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 코스_생성시_첫_좌표_끝_좌표만_존재할때_둘은_중복될_수_없다() {
            assertThatThrownBy(() -> new Course("코스", List.of(new Coordinate(0, 0), new Coordinate(0, 0))))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 세그먼트_분리_테스트 {

        @Test
        void 코스의_세그먼트를_생성한다() {
            var course = new Course("테스트코스", List.of(
                    new Coordinate(0, 0, 0),        // 시작점
                    new Coordinate(0, 0.0001, 10),  // 오르막 (UPHILL)
                    new Coordinate(0, 0.0002, 20),  // 오르막 (UPHILL)
                    new Coordinate(0, 0.0003, 20),  // 평지 (FLAT)
                    new Coordinate(0, 0.0002, 10),  // 내리막 (DOWNHILL)
                    new Coordinate(0, 0, 0)         // 시작점으로 돌아옴
            ));

            var segments = course.segments();

            assertThat(segments).hasSize(3);

            assertThat(segments.get(0).inclineType()).isEqualTo(InclineType.UPHILL);
            assertThat(segments.get(0).coordinates()).hasSize(3).containsExactly(
                    new Coordinate(0, 0, 0),        // 시작점
                    new Coordinate(0, 0.0001, 10),  // 오르막 (UPHILL)
                    new Coordinate(0, 0.0002, 20)   // 오르막 (UPHILL)
            );

            assertThat(segments.get(1).inclineType()).isEqualTo(InclineType.FLAT);
            assertThat(segments.get(1).coordinates()).hasSize(2).containsExactly(
                    new Coordinate(0, 0.0002, 20),  // 오르막 (UPHILL)
                    new Coordinate(0, 0.0003, 20)   // 평지 (FLAT)
            );

            assertThat(segments.get(2).inclineType()).isEqualTo(InclineType.DOWNHILL);
            assertThat(segments.get(2).coordinates()).hasSize(3).containsExactly(
                    new Coordinate(0, 0.0003, 20),  // 평지 (FLAT)
                    new Coordinate(0, 0.0002, 10),  // 내리막 (DOWNHILL)
                    new Coordinate(0, 0, 0)         // 시작점으로 돌아옴
            );
        }

        @Test
        void 동일한_경사타입과_방향의_세그먼트들이_올바르게_병합된다() {
            var course = new Course("병합테스트코스", List.of(
                    new Coordinate(0, 0, 0),
                    new Coordinate(0, 0.0001, 0.1),
                    new Coordinate(0, 0.0002, 0.2),
                    new Coordinate(0, 0.0001, 0.1),
                    new Coordinate(0, 0, 0)
            ));

            var segments = course.segments();

            // 같은 방향과 경사타입의 세그먼트들이 병합되어야 함
            assertThat(segments).hasSize(1);
        }
    }

    @ParameterizedTest
    @MethodSource("courseInfoAndExpectedDifficulty")
    void 코스의_난이도를_계산한다(List<Coordinate> coordinates, RoadType roadType, Difficulty expectedDifficulty) {
        var course = new Course("코스", roadType, coordinates);

        var difficulty = course.difficulty();

        assertThat(difficulty).isEqualTo(expectedDifficulty);
    }

    private static Stream<Arguments> courseInfoAndExpectedDifficulty() {
        return Stream.of(
                Arguments.of(
                        square(0, 0, 0.04, 0.04),
                        RoadType.트랙,
                        Difficulty.쉬움
                ),
                Arguments.of(
                        square(0, 0, 0.04, 0.04),
                        RoadType.보도,
                        Difficulty.보통
                ),
                Arguments.of(
                        square(0, 0, 0.04, 0.04),
                        RoadType.트레일,
                        Difficulty.어려움
                ),
                Arguments.of(
                        square(0, 0, 0.06, 0.06),
                        RoadType.트랙,
                        Difficulty.보통
                ),
                Arguments.of(
                        square(0, 0, 0.1, 0.1),
                        RoadType.트랙,
                        Difficulty.어려움
                )
        );
    }
}
