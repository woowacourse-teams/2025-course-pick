package coursepick.coursepick.application.dto;

import coursepick.coursepick.domain.Coordinate;

import java.util.List;

public record CourseInfo(
        String name,
        List<Coordinate> coordinates
) {
}
