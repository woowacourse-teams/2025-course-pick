package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.exception.ApplicationException;

import java.time.LocalDateTime;

public record ErrorResponse(
        String errorType,
        String message,
        String timestamp
) {
    public static ErrorResponse from(ApplicationException exception) {
        return new ErrorResponse(
                exception.errorType().name(),
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
    }

    public static ErrorResponse unexpected(Exception exception) {
        return new ErrorResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
    }
}
