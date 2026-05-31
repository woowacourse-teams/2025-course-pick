package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Meter;

import java.util.List;

public record DraftRouteWebResponse(
        List<CoordinateWebResponse> coordinates,
        double length
) {
    public static DraftRouteWebResponse of(List<Coordinate> coordinates, Meter totalLength) {
        return new DraftRouteWebResponse(
                CoordinateWebResponse.from(coordinates),
                totalLength.value()
        );
    }
}
