package coursepick.coursepick.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CourseTest {

    @Nested
    class 생성_테스트 {

        private static final String LENGTH_31_NAME = "1234567890123456789012345678901";

        @Test
        void 코스를_생성한다() {
            assertThatCode(() -> new Course("코스이름", getNormalCoordinates()))
                    .doesNotThrowAnyException();
        }

        @Test
        void 앞_뒤_공백을_제거하여_생성한다() {
            var course = new Course(" 코스이름   ", getNormalCoordinates());
            assertThat(course.name().value()).isEqualTo("코스이름");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                LENGTH_31_NAME,
                "짧"
        })
        void 잘못된_길이의_이름으로_코스를_생성하면_예외가_발생한다(String name) {
            assertThatThrownBy(() -> new Course(name, getNormalCoordinates()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "코스 이름",
                "코스  이름",
                "코스   이름",
                "코스    이름",
        })
        void 이름의_연속공백을_한_칸으로_변환하여_코스를_생성한다(String name) {
            var course = new Course(name, getNormalCoordinates());
            assertThat(course.name().value()).isEqualTo("코스 이름");
        }

        @Test
        void 코스의_좌표의_개수가_2보다_적으면_예외가_발생한다() {
            assertThatThrownBy(() -> new Course("코스이름", List.of(new Coordinate(1d, 1d))))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 코스_생성시_첫_좌표_끝_좌표만_존재할때_둘은_중복될_수_없다() {
            Coordinate coordinate1 = new Coordinate(37.5049400, 126.9058000, 18.19);
            List<Coordinate> coordinates = List.of(
                    coordinate1,
                    coordinate1
            );
            assertThatThrownBy(() -> new Course("테스트 코스", coordinates))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        private static List<Coordinate> getNormalCoordinates() {
            return List.of(new Coordinate(1d, 1d), new Coordinate(2d, 2d));
        }
    }

    @Test
    void 코스의_총_거리를_계산할_수_있다() {
        var course = new Course("한강뛰어보자", List.of(
                new Coordinate(37.518400, 126.995600),
                new Coordinate(37.518000, 126.996500),
                new Coordinate(37.517500, 126.998000),
                new Coordinate(37.517000, 127.000000),
                new Coordinate(37.516500, 127.002000),
                new Coordinate(37.516000, 127.004500),
                new Coordinate(37.515500, 127.007000),
                new Coordinate(37.515000, 127.009500),
                new Coordinate(37.515500, 127.008000),
                new Coordinate(37.516000, 127.004700),
                new Coordinate(37.516500, 127.002300),
                new Coordinate(37.517000, 127.001000),
                new Coordinate(37.517500, 126.997000),
                new Coordinate(37.518000, 126.994500),
                new Coordinate(37.518400, 126.993600)
        ));

        var totalLength = course.length();

        assertThat((int) totalLength.value()).isEqualTo(2924);
    }

    @ParameterizedTest
    @CsvSource({
            "37.517712, 126.995012, 142",
            "37.516678, 126.997065, 46"
    })
    void 특정_좌표에서_코스까지_가장_가까운_거리를_계산할_수_있다(double latitude, double longitude, int expectedDistance) {
        var course = new Course("한강뛰어보자", List.of(
                new Coordinate(37.519760, 126.995477),
                new Coordinate(37.517083, 126.997182),
                new Coordinate(37.519760, 126.995477)
        ));
        var target = new Coordinate(latitude, longitude);

        var distance = course.distanceFrom(target);

        assertThat((int) distance.value()).isEqualTo(expectedDistance);
    }

    @Test
    void 코스_위의_점에서_코스까지의_거리는_0에_가깝다() {
        var course = new Course("직선코스", List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.5, 127.001),
                new Coordinate(37.5, 127.0)
        ));
        var target = new Coordinate(37.5, 127.0005); // 코스 선분의 중점

        var distance = course.distanceFrom(target);

        assertThat(distance.value()).isLessThan(1.0);
    }

    @Test
    void 코스_외부_멀리_떨어진_점에서_코스까지의_거리를_계산한다() {
        var course = new Course("작은원형코스", List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.5001, 127.0),
                new Coordinate(37.5, 127.0001),
                new Coordinate(37.4999, 127.0),
                new Coordinate(37.5, 127.0)
        ));
        var target = new Coordinate(37.52, 127.02); // 매우 멀리 떨어진 점

        var distance = course.distanceFrom(target);

        assertThat((int) distance.value()).isEqualTo(2829);
    }

    @Test
    void 코스_내부의_점에서_코스까지의_거리를_계산한다() {
        var course = new Course("사각형코스", List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.501, 127.0),
                new Coordinate(37.501, 127.001),
                new Coordinate(37.5, 127.001),
                new Coordinate(37.5, 127.0)
        ));
        var target = new Coordinate(37.5005, 127.0005); // 사각형 중앙

        var distance = course.distanceFrom(target);

        assertThat((int) distance.value()).isEqualTo(44);
    }

    @Test
    void 코스_시작점에서_코스까지의_거리는_0이다() {
        var course = new Course("삼각형코스", List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.501, 127.0),
                new Coordinate(37.5005, 127.001),
                new Coordinate(37.5, 127.0)
        ));
        var target = new Coordinate(37.5, 127.0);

        var distance = course.distanceFrom(target);

        assertThat(distance.value()).isEqualTo(0.0);
    }

    @ParameterizedTest
    @CsvSource({
            "37.4999, 126.9999, 14",   // 코스 바로 옆
            "37.5001, 127.0001, 0",    // 코스 반대편 바로 옆 (코스 위의 점)
            "37.5, 126.999, 88"        // 코스에서 서쪽으로 100m
    })
    void 코스_주변_다양한_위치에서의_거리를_계산한다(double latitude, double longitude, int expectedDistance) {
        var course = new Course("정사각형코스", List.of(
                new Coordinate(37.5, 127.0),
                new Coordinate(37.5001, 127.0),
                new Coordinate(37.5001, 127.0001),
                new Coordinate(37.5, 127.0001),
                new Coordinate(37.5, 127.0)
        ));
        var target = new Coordinate(latitude, longitude);

        var distance = course.distanceFrom(target);

        assertThat((int) distance.value()).isEqualTo(expectedDistance);
    }

    @ParameterizedTest
    @CsvSource({
            "-10, -10, 0, 0",
            "20, 20, 10, 10",
            "10, 0, 5, 5",
            "5, 0, 2.5, 2.5"
    })
    void 코스의_좌표_중에서_가장_가까운_좌표를_계산한다(double targetLatitude, double targetLongitude, double latitude, double longitude) {
        Course course = new Course("왕복코스", List.of(
                new Coordinate(0, 0),
                new Coordinate(10, 10.0),
                new Coordinate(0, 0)
        ));
        Coordinate target = new Coordinate(targetLatitude, targetLongitude);

        Coordinate minDistanceCoordinate = course.closestCoordinateFrom(target);

        Coordinate expectedCoordinate = new Coordinate(latitude, longitude);
        assertThat(minDistanceCoordinate).isEqualTo(expectedCoordinate);
    }

    @ParameterizedTest
    @MethodSource("createArguments")
    void 코스의_난이도를_계산한다(List<Coordinate> coordinates, RoadType roadType, Difficulty expectedDifficulty) {
        var course = new Course("코스", roadType, coordinates);

        var difficulty = course.difficulty();

        assertThat(difficulty).isEqualTo(expectedDifficulty);
    }

    private static Stream<Arguments> createArguments() {
        return Stream.of(
                Arguments.of(
                        List.of(new Coordinate(37.5, 127.0), new Coordinate(37.499999, 127.0), new Coordinate(37.5, 127.0)),
                        RoadType.트랙,
                        Difficulty.쉬움
                ),
                Arguments.of(
                        List.of(new Coordinate(37.5, 127.0), new Coordinate(37.499999, 127.0), new Coordinate(37.5, 127.0)),
                        RoadType.트레일,
                        Difficulty.쉬움
                ),
                Arguments.of(
                        List.of(new Coordinate(37.5, 127.0), new Coordinate(37.499999, 127.0), new Coordinate(37.5, 127.0)),
                        RoadType.보도,
                        Difficulty.쉬움
                ),
                Arguments.of(
                        List.of(new Coordinate(37.499384, 126.999433), new Coordinate(37.501806, 127.239550), new Coordinate(37.499384, 126.999433)),
                        RoadType.보도,
                        Difficulty.어려움
                ),
                Arguments.of(
                        List.of(new Coordinate(37.506591, 127.145630), new Coordinate(37.311405, 127.383810), new Coordinate(37.506591, 127.145630)),
                        RoadType.트랙,
                        Difficulty.어려움
                ),
                Arguments.of(
                        List.of(new Coordinate(37.486225, 127.063228), new Coordinate(37.486557, 127.187908), new Coordinate(37.486225, 127.063228)),
                        RoadType.트레일,
                        Difficulty.어려움
                )
        );
    }

    @Nested
    class 경사도_요약_테스트 {

        @ParameterizedTest
        @MethodSource(value = {
                "flatCoordinates",
                "repeatingHillsCoordinates"
        })
        void 코스의_경사도_요약을_계산한다(List<Coordinate> coordinates, InclineSummary expectedInclineSummary) {
            Course course = new Course("코스이름1", coordinates);

            InclineSummary inclineSummary = course.inclineSummary();

            assertThat(inclineSummary).isSameAs(expectedInclineSummary);
        }

        private static Stream<Arguments> flatCoordinates() {
            List<Coordinate> coordinates = List.of(
                    new Coordinate(0, 0, 0),
                    new Coordinate(10, 10, 0)
            );
            return Stream.of(Arguments.of(coordinates, InclineSummary.MOSTLY_FLAT));
        }

        private static Stream<Arguments> repeatingHillsCoordinates() {
            List<Coordinate> coordinates = List.of(
                    new Coordinate(0, 0, 0),
                    new Coordinate(0, 0.0009, 8.8),
                    new Coordinate(0, 0, 0)
            );
            return Stream.of(Arguments.of(coordinates, InclineSummary.REPEATING_HILLS));
        }

    }

    @Nested
    class 세그먼트_분리_테스트 {

        @Test
        void 코스의_세그먼트를_생성한다() {
            Course course = new Course("테스트코스", List.of(
                    new Coordinate(0, 0, 0),        // 시작점
                    new Coordinate(0, 0.0001, 10),  // 오르막 (UPHILL)
                    new Coordinate(0, 0.0002, 20),  // 오르막 (UPHILL)
                    new Coordinate(0, 0.0003, 20),  // 평지 (FLAT)
                    new Coordinate(0, 0.0002, 10),  // 내리막 (DOWNHILL)
                    new Coordinate(0, 0, 0)         // 시작점으로 돌아옴
            ));

            List<Segment> segments = course.segments();

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
            Course course = new Course("병합테스트코스", List.of(
                    new Coordinate(0, 0, 0),
                    new Coordinate(0, 0.0010, 8),   // 오르막 (약 4.5도)
                    new Coordinate(0, 0.0020, 16),  // 오르막 (약 4.5도)
                    new Coordinate(0, 0.0010, 8),   // 내리막 (약 -4.5도)
                    new Coordinate(0, 0, 0)         // 내리막 (약 -4.5도)
            ));

            List<Segment> segments = course.segments();

            // 같은 방향과 경사타입의 세그먼트들이 병합되어야 함
            assertThat(segments).hasSize(1);
        }
    }
}
