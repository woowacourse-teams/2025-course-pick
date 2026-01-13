package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "좌표 스냅 요청")
public record SnapWebRequest(
        @Schema(description = "도로에 스냅할 좌표 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<CoordinateWebRequest> coordinates
) {
}
