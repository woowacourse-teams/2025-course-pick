package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignWebResponse(
        @Schema(description = "사용자 ID", example = "65f0c2a7e1b1c2a3b4c5d6e7")
        String userId,
        @Schema(description = "런세권 엑세스토큰", example = "123456789")
        String accessToken
) {
}
