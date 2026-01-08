package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;

import java.util.List;

public record CoordinatesSnapWebResponse(
        List<AdminCoordinateWebResponse> matchedCoordinates
) {
    public static CoordinatesSnapWebResponse from(List<Coordinate> coordinates) {
        List<AdminCoordinateWebResponse> matchedCoordinates = coordinates.stream()
                .map(AdminCoordinateWebResponse::from)
                .toList();

        return new CoordinatesSnapWebResponse(matchedCoordinates);
    }
}
