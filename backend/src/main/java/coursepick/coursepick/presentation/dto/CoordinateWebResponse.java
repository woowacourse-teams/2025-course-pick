package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CoordinateWebResponse(
        @Schema(description = "위도 (-90 ~ 90)", example = "37.514167")
        double latitude,
        @Schema(description = "경도 (-180 ~ 180)", example = "127.103611")
        double longitude
) {
    public static CoordinateWebResponse from(Coordinate coordinate) {
        return new CoordinateWebResponse(coordinate.latitude(), coordinate.longitude());
    }

    public static List<CoordinateWebResponse> from(List<Coordinate> coordinates) {
        return coordinates.stream()
                .map(CoordinateWebResponse::from)
                .toList();
    }
}
