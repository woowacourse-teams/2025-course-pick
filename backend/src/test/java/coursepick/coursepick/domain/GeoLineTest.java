package coursepick.coursepick.domain;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.GeoLine;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

class GeoLineTest {

    @Nested
    class 거리_계산_테스트 {

        @Test
        void 시작좌표와_끝좌표_사이의_거리를_계산한다() {
            var start = new Coordinate(0, 0);
            var end = left(start, 1000);
            var geoLine = new GeoLine(start, end);

            var distance = geoLine.length();

            assertThat(distance.value()).isCloseTo(1000, withPercentage(1));
        }

        @Test
        void 시작좌표와_끝좌표가_같으면_거리는_0이다() {
            var start = new Coordinate(0, 0);
            var geoLine = new GeoLine(start, start);

            var distance = geoLine.length();

            assertThat(distance.value()).isCloseTo(0, withPercentage(1));
        }
    }

    @Nested
    class 가장_가까운_좌표_계산_테스트 {

        public static Stream<Arguments> targetAndExpectedCoordinate() {
            var start = new Coordinate(0, 0);
            return Stream.of(
                    Arguments.of(upleft(start, 300), left(start, 300)),
                    Arguments.of(downleft(start, 300), left(start, 300))
            );
        }

        @ParameterizedTest
        @MethodSource("targetAndExpectedCoordinate")
        void 가장_가까운_좌표를_계산한다(Coordinate target, Coordinate expectedCoordinate) {
            var start = new Coordinate(0, 0);
            var end = left(start, 1000);
            var geoLine = new GeoLine(start, end);

            var result = geoLine.closestCoordinateFrom(target);

            assertThat(result).isEqualTo(expectedCoordinate);
        }

        @Test
        void 기준_좌표가_선분의_시작점_이전에_있으면_시작점을_반환한다() {
            var start = new Coordinate(0, 0);
            var end = left(start, 1000);
            var target = right(start, 1000);
            var geoLine = new GeoLine(start, end);

            var result = geoLine.closestCoordinateFrom(target);

            assertThat(start).isEqualTo(result);
        }

        @Test
        void 기준_좌표가_선분의_끝점_이후에_있으면_끝점을_반환한다() {
            var start = new Coordinate(0, 0);
            var end = left(start, 1000);
            var target = left(end, 1000);
            var geoLine = new GeoLine(start, end);

            var result = geoLine.closestCoordinateFrom(target);

            assertThat(end).isEqualTo(result);
        }
    }
}
