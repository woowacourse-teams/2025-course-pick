package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;

import java.util.List;

public record CoordinatesMatchWebResponse(
        List<AdminCoordinateWebResponse> matchedCoordinates
) {
    public static CoordinatesMatchWebResponse from(List<Coordinate> coordinates) {
        List<AdminCoordinateWebResponse> matchedCoordinates = coordinates.stream()
                .map(coordinate -> new Coordinate(coordinate.latitude(), coordinate.longitude(), coordinate.elevation()))
                .map(AdminCoordinateWebResponse::from)
                .toList();

        return new CoordinatesMatchWebResponse(matchedCoordinates);
    }
}
