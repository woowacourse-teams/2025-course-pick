package coursepick.coursepick.domain.course;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static coursepick.coursepick.test_util.CoordinateTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

class DraftSegmentTest {

    @Nested
    class 생성_테스트 {

        @Test
        void 좌표_목록으로_드래프트_세그먼트를_생성한다() {
            var start = new Coordinate(0, 0);
            var end = right(start, 1000);

            var segment = DraftSegment.of(List.of(start, end));

            assertThat(segment.length().value()).isCloseTo(1000, withPercentage(1));
        }

        @Test
        void 여러_좌표로_이루어진_경로의_총_거리를_계산한다() {
            var start = new Coordinate(0, 0);
            var mid = right(start, 1000);
            var end = right(mid, 1000);

            var segment = DraftSegment.of(List.of(start, mid, end));

            assertThat(segment.length().value()).isCloseTo(2000, withPercentage(1));
        }
    }

    @Nested
    class 병합_테스트 {

        @Test
        void 두_세그먼트를_병합하면_좌표가_합쳐진다() {
            var start = new Coordinate(0, 0);
            var mid = right(start, 1000);
            var end = right(mid, 1000);
            var segment1 = DraftSegment.of(List.of(start, mid));
            var segment2 = DraftSegment.of(List.of(mid, end));

            var merged = segment1.merge(segment2);

            assertThat(merged.coordinates()).hasSize(
                    segment1.coordinates().size() + segment2.coordinates().size()
            );
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
}
