package coursepick.coursepick.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CoordinateTest {

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

    @Test
    void 좌표와_좌표_사이의_거리를_계산할수있다() {
        var lutherBuilding = new Coordinate(37.515348, 127.103015);
        var seokchonLake = new Coordinate(37.511314, 127.105203);

        var distance = lutherBuilding.distanceFrom(seokchonLake);

        assertThat((int) distance).isEqualTo(488);
    }
}
