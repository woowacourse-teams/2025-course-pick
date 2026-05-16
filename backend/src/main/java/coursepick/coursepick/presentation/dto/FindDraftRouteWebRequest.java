package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FindDraftRouteWebRequest(
        @NotNull @Valid
        CoordinateWebRequest origin,

        @NotNull @Valid
        CoordinateWebRequest destination
) {
    public List<Coordinate> toCoordinates() {
        return List.of(origin.toCoordinate(), destination.toCoordinate());
    }
}
