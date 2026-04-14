package coursepick.coursepick.presentation.dto;

import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record ErrorResponse(
        String message,
        String timestamp
) {

    private static final Pattern DUP_KEY_PATTERN = Pattern.compile("dup key: \\{\\s*(.*?)\\s*}");

    public static ErrorResponse
    from(Exception exception) {
        return new ErrorResponse(
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
    }

    public static ErrorResponse from(BindingResult bindingResult) {
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse(errorMessage, LocalDateTime.now().toString());
    }

    public static ErrorResponse fromDuplicatedKey(String errorDescription) {
        String responseMessage = "이미 존재하는 데이터입니다.";
        if (errorDescription != null) {
            Matcher matcher = DUP_KEY_PATTERN.matcher(errorDescription);
            if (matcher.find()) {
                // "" 제거
                String cause = matcher.group(1).replace("\"", "").trim();

                //중복 값 추출
                String[] parts = cause.split(":", 2);
                if (parts.length == 2) {
                    String duplicateValue = parts[1].trim();
                    responseMessage = "'%s'은(는) 이미 존재하는 데이터입니다.".formatted(duplicateValue);
                } else {
                    responseMessage += " " + cause;
                }
            }
        }
        return new ErrorResponse(responseMessage, LocalDateTime.now().toString());
    }
}
