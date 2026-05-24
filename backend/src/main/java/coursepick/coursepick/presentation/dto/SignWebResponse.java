package coursepick.coursepick.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignWebResponse(
        @Schema(description = "런세권 엑세스토큰", example = "123456789")
        String accessToken,
        @Schema(description = "유저 ID", example = "689c3143182cecc6353cca7b")
        String userId
) {
}
