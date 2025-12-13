package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;

import java.util.List;

public record CoordinatesMatchWebRequest(
        List<Coordinate> coordinates
) {
}
