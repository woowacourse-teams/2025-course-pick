package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Meter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DraftRouteWebResponse(
        @Schema(description = "전체 경로 좌표 목록")
        List<CoordinateWebResponse> coordinates,
        @Schema(description = "총 거리(미터)", example = "1250.5")
        double length
) {
    public static DraftRouteWebResponse of(List<Coordinate> coordinates, Meter totalLength) {
        return new DraftRouteWebResponse(
                CoordinateWebResponse.from(coordinates),
                totalLength.value()
        );
    }
}
