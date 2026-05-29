package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.List;

public record CoordinateWebResponse(
        double latitude,
        double longitude
) {
    public static List<CoordinateWebResponse> from(List<Coordinate> coordinates) {
        return coordinates.stream()
                .map(CoordinateWebResponse::from)
                .toList();
    }

    public static CoordinateWebResponse from(Coordinate coordinate) {
        return new CoordinateWebResponse(coordinate.latitude(), coordinate.longitude());
    }
}
