package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FindDraftRouteWebRequest(
        @NotNull
        @Size(min = 2, message = "경로는 2개 이상의 좌표로 구성되어야 합니다.")
        @Valid
        @Schema(description = "지금까지 찍은 경로 좌표 목록 (순서대로)")
        List<CoordinateWebRequest> routePoints
) {
    public List<Coordinate> toCoordinates() {
        return routePoints.stream()
                .map(CoordinateWebRequest::toCoordinate)
                .toList();
    }
}
