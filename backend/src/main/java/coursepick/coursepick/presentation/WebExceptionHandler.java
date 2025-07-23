package coursepick.coursepick.presentation;

import coursepick.coursepick.presentation.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class WebExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.info("{}", exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.info("{}", exception.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("예상하지 못한 예외: {}", exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(ErrorResponse.from(exception));
    }
}
