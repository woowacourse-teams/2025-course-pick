package coursepick.coursepick.presentation.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String timestamp
) {
    public static ErrorResponse from(Exception exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
    }
}
