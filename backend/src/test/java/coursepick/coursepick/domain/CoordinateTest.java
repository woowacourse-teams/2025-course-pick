package coursepick.coursepick.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class CoordinateTest {

    @Nested
    class 생성_테스트 {

        @Test
        void 생성한다() {
            assertThatCode(() -> new Coordinate(80, 100))
                    .doesNotThrowAnyException();
        }

        @Test
        void 위도의_범위가_벗어나면_예외가_발생한다() {
            assertThatThrownBy(() -> new Coordinate(91, 30))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Coordinate(-91, 30))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 경도의_범위가_벗어나면_예외가_발생한다() {
            assertThatThrownBy(() -> new Coordinate(80, 180))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Coordinate(80, -181))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 생성하면_위도와_경도가_소수점_이하_6자리까지만_남는다() {
            Coordinate coordinate = new Coordinate(80.123456789, 100.123456789);

            assertThat(coordinate.latitude()).isEqualTo(80.123456);
            assertThat(coordinate.longitude()).isEqualTo(100.123456);
        }
    }

    @Nested
    class 거리_계산_테스트 {

        @ParameterizedTest
        @CsvSource({
                "37.566535, 126.977969, 37.565223, 126.978946, 169",  // 경복궁 - 광화문
                "37.579617, 126.977041, 37.566535, 126.977969, 1456", // 종각 - 경복궁
                "37.540705, 127.071917, 37.513294, 127.058000, 3285", // 성수역 - 잠실역
                "35.158170, 129.160179, 35.179665, 129.075986, 8017", // 부산역 - 해운대역
                "37.551224, 126.988243, 37.497952, 127.027619, 6866"  // 명동 - 강남역
        })
        void 좌표와_좌표_사이의_거리를_계산한다(double lat1, double lng1, double lat2, double lng2, int expectedDistance) {
            var coord1 = new Coordinate(lat1, lng1);
            var coord2 = new Coordinate(lat2, lng2);

            var distance = coord1.distanceFrom(coord2);

            assertThat((int) distance).isEqualTo(expectedDistance);
        }

        @Test
        void 동일한_좌표의_거리는_0이다() {
            var coord1 = new Coordinate(37.515348, 127.103015);
            var coord2 = new Coordinate(37.515348, 127.103015);

            var distance = coord1.distanceFrom(coord2);

            assertThat(distance).isEqualTo(0.0);
        }

        @Test
        void 매우_가까운_좌표의_거리를_계산한다() {
            var coord1 = new Coordinate(37.515348, 127.103015);
            var coord2 = new Coordinate(37.515349, 127.103016);

            var distance = coord1.distanceFrom(coord2);

            assertThat(distance).isLessThan(1.0).isGreaterThan(0.0);
        }

        @Test
        void 소수점_6자리_정밀도_내에서의_거리_계산() {
            var coord1 = new Coordinate(37.515348123456, 127.103015123456);
            var coord2 = new Coordinate(37.515348123457, 127.103015123457);

            var distance = coord1.distanceFrom(coord2);

            assertThat(distance).isEqualTo(0.0);
        }

        @Test
        void 반대편_지구에서의_거리를_계산한다() {
            var seoul = new Coordinate(37.5665, 126.9780);
            var antipode = new Coordinate(-37.5665, -53.0220);

            var distance = seoul.distanceFrom(antipode);

            assertThat((int) distance).isEqualTo(20015086);
        }
    }

    @Nested
    class 위도_경도_비교_테스트 {

        @Test
        void 동일한_위도_경도를_가진_좌표는_같다고_판단한다() {
            var coordinate1 = new Coordinate(37.515348, 127.103015);
            var coordinate2 = new Coordinate(37.515348, 127.103015);

            boolean result = coordinate1.hasSameLatitudeAndLongitude(coordinate2);

            assertThat(result).isTrue();
        }

        @Test
        void 다른_위도_경도를_가진_좌표는_다르다고_판단한다() {
            var coordinate1 = new Coordinate(37.515348, 127.103015);
            var coordinate2 = new Coordinate(37.515348, 127.103016);

            boolean result = coordinate1.hasSameLatitudeAndLongitude(coordinate2);

            assertThat(result).isFalse();
        }

        @Test
        void 소수점_6자리까지_정밀도로_좌표_비교한다() {
            var coordinate1 = new Coordinate(37.515348123456, 127.103015123456);
            var coordinate2 = new Coordinate(37.515348123999, 127.103015123999);

            boolean result = coordinate1.hasSameLatitudeAndLongitude(coordinate2);

            assertThat(result).isTrue();
        }
    }

    @Nested
    class 거리_비율_계산_테스트 {

        @ParameterizedTest
        @CsvSource({
                "0.0, 0.0, 1.0, 1.0, 0.0",
                "0.5, 0.5, 1.0, 1.0, 0.5",
                "1.0, 1.0, 1.0, 1.0, 1.0",
                "-0.5, -0.5, 1.0, 1.0, -0.5",
                "1.5, 1.5, 1.0, 1.0, 1.5"
        })
        void 선분에서_특정_점까지의_거리_비율을_계산한다(double targetLat, double targetLng, double endLat, double endLng, double expectedRatio) {
            var target = new Coordinate(targetLat, targetLng);
            var start = new Coordinate(0.0, 0.0);
            var end = new Coordinate(endLat, endLng);

            double ratio = target.calculateDistanceRatioBetween(start, end);

            assertThat(ratio).isEqualTo(expectedRatio);
        }

        @Test
        void 선분_시작점에서_거리_비율을_계산하면_0이다() {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);
            var target = new Coordinate(1.0, 1.0);

            double ratio = target.calculateDistanceRatioBetween(start, end);

            assertThat(ratio).isEqualTo(0.0);
        }

        @Test
        void 선분_끝점에서_거리_비율을_계산하면_1이다() {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);
            var target = new Coordinate(3.0, 3.0);

            double ratio = target.calculateDistanceRatioBetween(start, end);

            assertThat(ratio).isEqualTo(1.0);
        }
    }

    @Nested
    class 거리_비율만큼_이동_테스트 {

        @Test
        void 거리_비율_0으로_이동하면_시작점이_된다() {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);

            var result = start.moveTo(end, 0.0);

            assertThat(result.latitude()).isEqualTo(1.0);
            assertThat(result.longitude()).isEqualTo(1.0);
        }

        @Test
        void 거리_비율_1로_이동하면_끝점이_된다() {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);

            var result = start.moveTo(end, 1.0);

            assertThat(result.latitude()).isEqualTo(3.0);
            assertThat(result.longitude()).isEqualTo(3.0);
        }

        @Test
        void 거리_비율_0점5로_이동하면_중간점이_된다() {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);

            var result = start.moveTo(end, 0.5);

            assertThat(result.latitude()).isEqualTo(2.0);
            assertThat(result.longitude()).isEqualTo(2.0);
        }

        @ParameterizedTest
        @CsvSource({
                "-0.5, 0.0, 0.0",
                "1.5, 4.0, 4.0",
                "2.0, 5.0, 5.0"
        })
        void 거리_비율이_0과_1_범위를_벗어나도_정상적으로_이동한다(double ratio, double expectedLat, double expectedLng) {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);

            var result = start.moveTo(end, ratio);

            assertThat(result.latitude()).isEqualTo(expectedLat);
            assertThat(result.longitude()).isEqualTo(expectedLng);
        }
    }
}
