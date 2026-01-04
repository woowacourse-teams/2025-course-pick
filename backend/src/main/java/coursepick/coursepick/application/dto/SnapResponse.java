package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.course.Coordinate;

import coursepick.coursepick.domain.course.Meter;

import java.util.List;

public record SnapResponse(
        List<Coordinate> coordinates,
        double length
) {
}
