package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import jakarta.validation.constraints.NotNull;

public record CoordinateWebRequest(
        @NotNull
        Double latitude,
        @NotNull
        Double longitude
) {
    public Coordinate toCoordinate() {
        return new Coordinate(latitude, longitude);
    }
}
