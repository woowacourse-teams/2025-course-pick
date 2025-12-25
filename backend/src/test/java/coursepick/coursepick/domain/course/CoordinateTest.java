package coursepick.coursepick.domain.course;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class CoordinateTest {

    @ParameterizedTest
    @CsvSource({
            "0.5, 5, 5, 5",
            "1, 10, 10, 10",
            "1.5, 15, 15, 15",
    })
    void 두_점에_대하여_선형보간한다(double lerpRatio, double expectedLatitude, double expectedLongitude, double expectedElevation) {
        var start = new Coordinate(0, 0, 0);
        var end = new Coordinate(10, 10, 10);

        var result = Coordinate.lerp(start, end, lerpRatio);

        assertThat(result.latitude()).isEqualTo(expectedLatitude);
        assertThat(result.longitude()).isEqualTo(expectedLongitude);
        assertThat(result.elevation()).isEqualTo(expectedElevation);
    }

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
        void 생성하면_위도와_경도가_소수점_이하_7자리까지만_남는다() {
            Coordinate coordinate = new Coordinate(80.123456789, 100.123456789);

            assertThat(coordinate.latitude()).isEqualTo(80.1234567);
            assertThat(coordinate.longitude()).isEqualTo(100.1234567);
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

            double ratio = target.projectionRatioBetween(start, end);

            assertThat(ratio).isEqualTo(expectedRatio);
        }

        @Test
        void 선분_시작점에서_거리_비율을_계산하면_0이다() {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);
            var target = new Coordinate(1.0, 1.0);

            double ratio = target.projectionRatioBetween(start, end);

            assertThat(ratio).isEqualTo(0.0);
        }

        @Test
        void 선분_끝점에서_거리_비율을_계산하면_1이다() {
            var start = new Coordinate(1.0, 1.0);
            var end = new Coordinate(3.0, 3.0);
            var target = new Coordinate(3.0, 3.0);

            double ratio = target.projectionRatioBetween(start, end);

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
