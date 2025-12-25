package coursepick.coursepick.domain.course;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InclineSummaryTest {

    @ParameterizedTest
    @MethodSource(value = {
            "flatSegments",
            "repeatingHillsSegments",
            "sometimesUphillSegments",
            "sometimesDownhillSegments",
            "continuousUphillSegments",
            "continuousDownhillSegments"
    })
    void 경사_요약_정보를_결정한다(List<Segment> segments, InclineSummary expectedInclineSummary) {
        InclineSummary inclineSummary = InclineSummary.of(segments);

        assertThat(inclineSummary).isEqualTo(expectedInclineSummary);
    }

    private static Stream<Arguments> flatSegments() {
        List<Segment> segments = List.of(
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(10, 10, 0)
                        )
                ))
        );
        return Stream.of(Arguments.of(segments, InclineSummary.MOSTLY_FLAT));
    }

    private static Stream<Arguments> repeatingHillsSegments() {
        List<Segment> segments = List.of(
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.0009, -8.8)
                        )
                )),
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.0009, 8.8)
                        )
                ))
        );
        return Stream.of(Arguments.of(segments, InclineSummary.REPEATING_HILLS));
    }

    private static Stream<Arguments> sometimesUphillSegments() {
        List<Segment> segments = List.of(
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.001, 0)
                        )
                )),
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.0009, 8.8)
                        )
                ))
        );
        return Stream.of(Arguments.of(segments, InclineSummary.SOMETIMES_UPHILL));
    }

    private static Stream<Arguments> sometimesDownhillSegments() {
        List<Segment> segments = List.of(
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.001, 0)
                        )
                )),
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.0009, -8.8)
                        )
                ))
        );
        return Stream.of(Arguments.of(segments, InclineSummary.SOMETIMES_DOWNHILL));
    }

    private static Stream<Arguments> continuousUphillSegments() {
        List<Segment> segments = List.of(
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.0009, 0)
                        )
                )),
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.001, 10)
                        )
                ))
        );
        return Stream.of(Arguments.of(segments, InclineSummary.CONTINUOUS_UPHILL));
    }

    private static Stream<Arguments> continuousDownhillSegments() {
        List<Segment> segments = List.of(
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.0009, 0)
                        )
                )),
                new Segment(List.of(
                        new GeoLine(
                                new Coordinate(0, 0, 0), new Coordinate(0, 0.001, -10)
                        )
                ))
        );
        return Stream.of(Arguments.of(segments, InclineSummary.CONTINUOUS_DOWNHILL));
    }
}
