package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FindDraftRouteWebRequest(
        @NotNull @Valid
        @Schema(description = "직전 포인트")
        CoordinateWebRequest origin,

        @NotNull @Valid
        @Schema(description = "새로 찍은 포인트")
        CoordinateWebRequest destination
) {
    public List<Coordinate> toCoordinates() {
        return List.of(origin.toCoordinate(), destination.toCoordinate());
    }
}
