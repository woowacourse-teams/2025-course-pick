package coursepick.coursepick.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginWebRequest(
        @NotBlank String password
) {
}
