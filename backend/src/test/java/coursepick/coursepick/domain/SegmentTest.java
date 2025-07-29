package coursepick.coursepick.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SegmentTest {

    @Test
    void 경향성이_같은_세그먼트끼리_합친다() {
        List<Segment> segments = List.of(
                Segment.create(new Coordinate(0, 0, 0), new Coordinate(1, 1, 10)),    // UP
                Segment.create(new Coordinate(1, 1, 10), new Coordinate(2, 2, 20)),   // UP
                Segment.create(new Coordinate(2, 2, 20), new Coordinate(3, 3, 10))    // DOWN
        );

        List<Segment> mergedSegments = Segment.mergeSameDirection(segments);

        assertThat(mergedSegments).hasSize(2);
        assertThat(mergedSegments.get(0).coordinates()).containsExactly(
                new Coordinate(0, 0, 0),
                new Coordinate(1, 1, 10),
                new Coordinate(2, 2, 20)
        );
        assertThat(mergedSegments.get(1).coordinates()).containsExactly(
                new Coordinate(2, 2, 20),
                new Coordinate(3, 3, 10)
        );
    }

    @Test
    void 경사타입이_같은_세그먼트끼리_합친다() {
        List<Segment> segments = List.of(
                Segment.create(new Coordinate(0, 0, 0), new Coordinate(0, 0.0009, 15)),       // UPHILL
                Segment.create(new Coordinate(0, 0.0009, 15), new Coordinate(0, 0.0018, 30)), // UPHILL
                Segment.create(new Coordinate(0, 0.0018, 30), new Coordinate(0, 0.0027, 30)), // FLAT
                Segment.create(new Coordinate(0, 0.0027, 30), new Coordinate(0, 0.0036, 30)), // FLAT
                Segment.create(new Coordinate(0, 0.0036, 30), new Coordinate(0, 0.0048, 15))  // DOWNHILL
        );

        List<Segment> mergedSegments = Segment.mergeSameInclineType(segments);

        assertThat(mergedSegments).hasSize(3);
        assertThat(mergedSegments.get(0).coordinates()).containsExactly(
                new Coordinate(0, 0, 0),
                new Coordinate(0, 0.0009, 15),
                new Coordinate(0, 0.0018, 30)
        );
        assertThat(mergedSegments.get(1).coordinates()).containsExactly(
                new Coordinate(0, 0.0018, 30),
                new Coordinate(0, 0.0027, 30),
                new Coordinate(0, 0.0036, 30)
        );
        assertThat(mergedSegments.get(2).coordinates()).containsExactly(
                new Coordinate(0, 0.0036, 30),
                new Coordinate(0, 0.0048, 15)
        );
    }
}
