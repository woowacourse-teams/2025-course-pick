package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.List;

public record SnapResponse(
        List<Coordinate> coordinates,
        double length
) {
}
