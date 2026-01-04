package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignWebResponse(
        @Schema(description = "런세권 엑세스토큰", example = "123456789")
        String accessToken
) {
}
