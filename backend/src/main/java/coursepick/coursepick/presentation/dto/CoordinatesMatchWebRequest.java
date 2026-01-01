package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.Coordinate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;

import java.util.List;

public record CoordinatesMatchWebRequest(
        @NotNull
        @Size(min = 2)
        List<Coordinate> coordinates
) {
}
