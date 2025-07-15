package coursepick.coursepick.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinateTest {

    @Test
    void 좌표와_좌표_사이의_거리를_계산할수있다() {
        var lutherBuilding = new Coordinate(37.515348, 127.103015);
        var seokchonLake = new Coordinate(37.511314, 127.105203);

        var distance = lutherBuilding.distanceFrom(seokchonLake);

        assertThat((int) distance).isEqualTo(488);
    }
}
