package coursepick.coursepick.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record SignWebRequest(
        @NotBlank(message = "액세스 토큰은 필수입니다.")
        String accessToken
) {
}
