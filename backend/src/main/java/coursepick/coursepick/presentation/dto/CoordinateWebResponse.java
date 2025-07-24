package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;

import java.util.List;

public record CoordinateWebResponse(
        double latitude,
        double longitude
) {
    public static List<CoordinateWebResponse> from(List<Coordinate> coordinates) {
        return coordinates.stream()
                .map(coordinate -> new CoordinateWebResponse(coordinate.latitude(), coordinate.longitude()))
                .toList();
    }
}
