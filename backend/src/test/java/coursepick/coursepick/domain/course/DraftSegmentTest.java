package coursepick.coursepick.domain.course;

import org.junit.jupiter.api.Test;

import java.util.List;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

class DraftSegmentTest {

    @Test
    void 두_세그먼트를_병합하면_좌표가_합쳐진다() {
        var start = new Coordinate(0, 0);
        var mid = right(start, 1000);
        var end = right(mid, 1000);

        var segment1 = DraftSegment.of(List.of(start, mid));
        var segment2 = DraftSegment.of(List.of(mid, end));

        var merged = segment1.merge(segment2);

        assertThat(merged.coordinates()).containsExactly(start, mid, end);
    }

    @Test
    void 두_세그먼트를_병합하면_거리가_합산된다() {
        var start = new Coordinate(0, 0);
        var mid = right(start, 1000);
        var end = right(mid, 1000);

        var segment1 = DraftSegment.of(List.of(start, mid));
        var segment2 = DraftSegment.of(List.of(mid, end));

        var merged = segment1.merge(segment2);

        assertThat(merged.length().value()).isCloseTo(
                segment1.length().value() + segment2.length().value(),
                withPercentage(1)
        );
    }
}
