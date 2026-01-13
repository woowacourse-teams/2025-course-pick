package coursepick.coursepick.domain.course;

import java.util.List;

public record SnapResult(
        List<Coordinate> coordinates,
        double length
) {
}
