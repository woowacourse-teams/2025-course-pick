package coursepick.coursepick.presentation.dto;

import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record ErrorResponse(
        String message,
        String errorCode,
        String timestamp) {

    private static final Pattern ERROR_CODE_PATTERN = Pattern.compile("\\[ErrorCode = ([^\\]]+)\\]");

    public static ErrorResponse from(Exception exception) {
        String message = exception.getMessage();
        Matcher matcher = ERROR_CODE_PATTERN.matcher(message);
        String errorCode = matcher.find() ? matcher.group(1) : "UNKNOWN_ERROR";

        return new ErrorResponse(
                message,
                errorCode,
                LocalDateTime.now().toString()
        );
    }

    public static ErrorResponse from(BindingResult bindingResult) {
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse(errorMessage, "INVALID_INPUT", LocalDateTime.now().toString());
    }
}
