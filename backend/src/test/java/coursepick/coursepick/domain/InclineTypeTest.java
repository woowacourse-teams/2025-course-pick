package coursepick.coursepick.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class InclineTypeTest {

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0, 0, 0.0009, 0, FLAT",          // 0도
            "0, 0, 0, 0, 0.0009, 3, FLAT",          // 1.7x도
            "0, 0, 0, 0, 0.0009, 7, FLAT",          // 4.0x도
            "0, 0, 0, 0, 0.0009, 8.8, UPHILL",      // 5.1x도
            "0, 0, 0, 0, 0.0009, 15, UPHILL",       // 8.5x도
            "0, 0, 0, 0, 0.0009, -15, DOWNHILL",    // -8.5x도
            "0, 0, 0, 0, 0.0009, -8.8, DOWNHILL",   // -5.1x도
            "0, 0, 0, 0, 0.0009, -7, FLAT",         // -4.0x도
            "0, 0, 0, 0, 0.0009, -3, FLAT"          // -1.7x도
    })
    void 두_좌표간_경사_타입을_계산한다(
            double startLat, double startLng, double startElev,
            double endLat, double endLng, double endElev,
            InclineType expected
    ) {
        var start = new Coordinate(startLat, startLng, startElev);
        var end = new Coordinate(endLat, endLng, endElev);

        var result = InclineType.of(start, end);

        assertThat(result).isEqualTo(expected);
    }
}
