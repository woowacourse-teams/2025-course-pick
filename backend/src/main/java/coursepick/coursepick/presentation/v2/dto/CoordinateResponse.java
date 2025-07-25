package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;

public record CoordinateResponse(
        @Schema(example = "37.509835") double latitude,
        @Schema(example = "127.102495") double longitude
) {
    public static CoordinateResponse from(Coordinate coordinate) {
        return new CoordinateResponse(coordinate.latitude(), coordinate.longitude());
    }
}
