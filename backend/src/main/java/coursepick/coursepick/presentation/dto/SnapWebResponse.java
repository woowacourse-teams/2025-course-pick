package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.SnapResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "좌표 스냅 응답")
public record SnapWebResponse(
        @Schema(description = "도로에 스냅된 좌표 목록")
        List<CoordinateWebResponse> coordinates,

        @Schema(description = "스냅된 경로의 총 거리 (미터)", example = "1234.5")
        double length
) {
    public static SnapWebResponse from(SnapResponse snapResponse) {
        List<CoordinateWebResponse> coordinateWebResponses = snapResponse.coordinates().stream()
                .map(CoordinateWebResponse::from)
                .toList();
        double length = snapResponse.length();

        return new SnapWebResponse(coordinateWebResponses, length);
    }
}
