package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoordinateResponse(
        @Schema(example = "37.509835") double latitude,
        @Schema(example = "127.102495") double longitude
) {
}
