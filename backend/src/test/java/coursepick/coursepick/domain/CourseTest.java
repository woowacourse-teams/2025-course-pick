package coursepick.coursepick.domain;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

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
                new Coordinate(0, 0),
                new Coordinate(0, 0.0001),
                new Coordinate(0.0001, 0.0001),
                new Coordinate(0.0001, 0),
                new Coordinate(0, 0)
        ));

        var distance = course.length();

        assertThat(distance.value()).isCloseTo(44.4, Percentage.withPercentage(1));
    }

    @ParameterizedTest
    @CsvSource({
            "0.0002, 0.0001, 11.1",
            "0.00005, 0.00005, 5.55",
    })
    void 특정_좌표에서_코스까지_가장_가까운_거리를_계산할_수_있다(double latitude, double longitude, double expectedDistance) {
        var course = new Course("한강뛰어보자", List.of(
                new Coordinate(0, 0),
                new Coordinate(0, 0.0001),
                new Coordinate(0.0001, 0.0001),
                new Coordinate(0.0001, 0),
                new Coordinate(0, 0)
        ));
        var target = new Coordinate(latitude, longitude);

        var distance = course.distanceFrom(target);

        assertThat(distance.value()).isCloseTo(expectedDistance, Percentage.withPercentage(1));
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
            "0.0002, 0.00005, 0.0001, 0.00005",
            "0.0002, 0.0002, 0.0001, 0.0001",
            "0.00007, 0.00005, 0.0001, 0.00005",
    })
    void 코스의_좌표_중에서_가장_가까운_좌표를_계산한다(double targetLatitude, double targetLongitude, double expectedLatitude, double expectedLongitude) {
        Course course = new Course("왕복코스", List.of(
                new Coordinate(0, 0),
                new Coordinate(0, 0.0001),
                new Coordinate(0.0001, 0.0001),
                new Coordinate(0.0001, 0),
                new Coordinate(0, 0)
        ));
        Coordinate target = new Coordinate(targetLatitude, targetLongitude);

        Coordinate minDistanceCoordinate = course.closestCoordinateFrom(target);

        Coordinate expectedCoordinate = new Coordinate(expectedLatitude, expectedLongitude);
        assertThat(minDistanceCoordinate).isEqualTo(expectedCoordinate);
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
                    new Coordinate(0, 0.0001, 0.1),
                    new Coordinate(0, 0.0002, 0.2),
                    new Coordinate(0, 0.0001, 0.1),
                    new Coordinate(0, 0, 0)
            ));

            List<Segment> segments = course.segments();

            // 같은 방향과 경사타입의 세그먼트들이 병합되어야 함
            assertThat(segments).hasSize(1);
        }
    }
}
