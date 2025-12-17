package coursepick.coursepick.presentation;

import coursepick.coursepick.logging.LogContent;
import coursepick.coursepick.presentation.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class WebExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(NoSuchElementException e) {
        log.warn("[EXCEPTION] NoSuchElementException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.from(e));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("[EXCEPTION] IllegalArgumentException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.badRequest().body(ErrorResponse.from(e));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("[EXCEPTION] MissingServletRequestParameterException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.badRequest().body(ErrorResponse.from(e));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
        log.warn("[EXCEPTION] SecurityException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[EXCEPTION] 핸들링 하지 못한 Exception 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.internalServerError().body(ErrorResponse.from(e));
    }
}
