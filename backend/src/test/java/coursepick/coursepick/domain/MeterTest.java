package coursepick.coursepick.domain;

import coursepick.coursepick.domain.course.Meter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeterTest {

    @Test
    void 다른_값과의_비율을_계산한다() {
        Meter meter = new Meter(100);
        Meter other = new Meter(50);

        double rate = meter.getRateOf(other);

        assertThat(rate).isEqualTo(0.5);
    }

    @Test
    void 다른_값과의_비율을_계산할_때_내_값이_0이면_예외가_발생한다() {
        Meter meter = Meter.zero();
        Meter other = new Meter(50);

        Assertions.assertThatThrownBy(() -> meter.getRateOf(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("0을 기준으로 비율을 계산할 수 없습니다.");

    }
}
