package coursepick.coursepick.presentation;

import coursepick.coursepick.application.exception.QueryTimeoutException;
import coursepick.coursepick.application.exception.UnauthorizedException;
import coursepick.coursepick.logging.LogContent;
import coursepick.coursepick.presentation.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.mongodb.DuplicateKeyException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("[EXCEPTION] MethodArgumentNotValidException 예외 응답 반환", LogContent.exception(e));
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errorMessage, LocalDateTime.now().toString()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("[EXCEPTION] DuplicateKeyException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("이미 존재하는 데이터입니다.", LocalDateTime.now().toString()));
    }


    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleQueryTimeoutException(QueryTimeoutException e) {
        log.warn("[EXCEPTION] QueryTimeoutException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorResponse.from(e));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
        log.warn("[EXCEPTION] SecurityException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.from(e));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(UnauthorizedException e) {
        log.warn("[EXCEPTION] UnauthorizedException 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[EXCEPTION] 핸들링 하지 못한 Exception 예외 응답 반환", LogContent.exception(e));
        return ResponseEntity.internalServerError().body(ErrorResponse.from(e));
    }
}
