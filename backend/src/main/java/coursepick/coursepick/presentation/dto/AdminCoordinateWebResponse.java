package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.List;

public record AdminCoordinateWebResponse(
        double latitude,
        double longitude
) {
    public static List<AdminCoordinateWebResponse> from(List<Coordinate> coordinates) {
        return coordinates.stream()
                .map(AdminCoordinateWebResponse::from)
                .toList();
    }

    public static AdminCoordinateWebResponse from(Coordinate coordinate) {
        return new AdminCoordinateWebResponse(coordinate.latitude(), coordinate.longitude());
    }
}
