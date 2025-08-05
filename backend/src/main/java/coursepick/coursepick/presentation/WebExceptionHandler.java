package coursepick.coursepick.presentation;

import coursepick.coursepick.presentation.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class WebExceptionHandler {
//
//    @ExceptionHandler
//    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
//        log.info("{}", exception.getMessage());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.from(exception));
//    }

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

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException exception) {
        log.warn("{}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("{}", exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(ErrorResponse.from(exception));
    }
}
