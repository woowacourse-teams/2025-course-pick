package coursepick.coursepick.domain;

import java.util.List;

public record Segment(
        InclineType inclineType,
        List<Coordinate> coordinates
) {
}
