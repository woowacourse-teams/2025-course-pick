package coursepick.coursepick.presentation.dto;

import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record ErrorResponse(
        String message,
        String timestamp) {

    private static final Pattern DUP_KEY_PATTERN = Pattern.compile("dup key: \\{\\s*(.*?)\\s*}");

    public static ErrorResponse from(Exception exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now().toString());
    }

    public static ErrorResponse from(BindingResult bindingResult) {
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse(errorMessage, LocalDateTime.now().toString());
    }
}
