package coursepick.coursepick.presentation.v2.dto;

import coursepick.coursepick.domain.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CoordinateWebResponse(
        @Schema(example = "37.514167")
        double latitude,
        @Schema(example = "127.103611")
        double longitude
) {
    public static List<CoordinateWebResponse> from(List<Coordinate> coordinates) {
        return coordinates.stream()
                .map(coordinate -> new CoordinateWebResponse(coordinate.latitude(), coordinate.longitude()))
                .toList();
    }
}
