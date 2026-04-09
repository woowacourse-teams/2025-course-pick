package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CoordinateWebRequest(
        @NotNull
        @Schema(description = "위도 (-90 ~ 90)", example = "37.514167")
        Double latitude,
        @NotNull
        @Schema(description = "경도 (-180 ~ 180)", example = "127.103611")
        Double longitude
) {
    public Coordinate toCoordinate() {
        return new Coordinate(latitude, longitude);
    }
}
