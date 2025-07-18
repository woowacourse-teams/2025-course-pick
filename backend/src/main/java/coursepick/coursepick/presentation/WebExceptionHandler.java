package coursepick.coursepick.presentation;

import coursepick.coursepick.application.exception.InvalidArgumentException;
import coursepick.coursepick.presentation.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(InvalidArgumentException exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity.internalServerError().body(ErrorResponse.unexpected(exception));
    }
}
