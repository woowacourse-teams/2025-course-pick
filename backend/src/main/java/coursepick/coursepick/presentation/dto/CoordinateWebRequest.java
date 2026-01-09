package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위도와 경도를 나타내는 좌표")
public record CoordinateWebRequest(
        @Schema(description = "위도 (-90 ~ 90)", example = "37.5180", requiredMode = Schema.RequiredMode.REQUIRED)
        double latitude,

        @Schema(description = "경도 (-180 ~ 180)", example = "127.0280", requiredMode = Schema.RequiredMode.REQUIRED)
        double longitude
) {
}
