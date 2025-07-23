package coursepick.coursepick.domain;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class GeoLineTest {

    @Test
    void 시작좌표와_끝좌표_사이의_거리를_계산한다() {
        Coordinate start = new Coordinate(37.509287, 127.098094);
        Coordinate end = new Coordinate(37.510485, 127.101572);
        GeoLine geoLine = new GeoLine(start, end);

        Meter distance = geoLine.length();

        assertThat(distance.value()).isCloseTo(334, Percentage.withPercentage(1));
    }

    @Test
    void 시작좌표와_끝좌표가_같으면_거리는_0이다() {
        Coordinate start = new Coordinate(37.509287, 127.098094);
        Coordinate end = new Coordinate(37.509287, 127.098094);
        GeoLine geoLine = new GeoLine(start, end);

        Meter distance = geoLine.length();

        assertThat(distance.value()).isCloseTo(0, Percentage.withPercentage(1));
    }

    @Nested
    class 기준_좌표에서_선분에_가장_가까운_좌표를_계산한다 {

        @Test
        void 기준_좌표가_선분의_시작점_이전에_있으면_시작점을_반환한다() {
            Coordinate start = new Coordinate(0.0, 0.0);
            Coordinate end = new Coordinate(10.0, 0.0);
            Coordinate target = new Coordinate(-5.0, 0.0);
            GeoLine geoLine = new GeoLine(start, end);

            Coordinate result = geoLine.minDistanceCoordinateTo(target);

            assertThat(start).isEqualTo(result);
        }

        @Test
        void 기준_좌표가_선분의_끝점_이후에_있으면_끝점을_반환한다() {
            Coordinate start = new Coordinate(0.0, 0.0);
            Coordinate end = new Coordinate(10.0, 0.0);
            Coordinate target = new Coordinate(15.0, 0.0);

            GeoLine geoLine = new GeoLine(start, end);

            Coordinate result = geoLine.minDistanceCoordinateTo(target);

            assertThat(end).isEqualTo(result);
        }

        @ParameterizedTest
        @CsvSource({
                "10, 0, 5, 5",
                "5, 0, 2.5, 2.5",
                "0, 8, 4, 4"
        })
        void 가장_가까운_좌표를_계산한다(double targetLatitude, double targetLongitude, double latitude, double longitude) {
            Coordinate start = new Coordinate(0.0, 0.0);
            Coordinate end = new Coordinate(10.0, 10.0);
            Coordinate target = new Coordinate(targetLatitude, targetLongitude);
            GeoLine geoLine = new GeoLine(start, end);

            Coordinate result = geoLine.minDistanceCoordinateTo(target);

            Coordinate expectedCoordinate = new Coordinate(latitude, longitude);
            assertThat(result).isEqualTo(expectedCoordinate);
        }
    }
}
