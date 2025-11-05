package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;

import java.util.List;

public record AdminCoordinateWebResponse(
        double latitude,
        double longitude,
        double elevation
) {
    public static AdminCoordinateWebResponse from(Coordinate coordinate) {
        return new AdminCoordinateWebResponse(coordinate.latitude(), coordinate.longitude(), coordinate.elevation());
    }

    public static List<AdminCoordinateWebResponse> from(List<Coordinate> coordinates) {
        return coordinates.stream()
                .map(AdminCoordinateWebResponse::from)
                .toList();
    }
}
