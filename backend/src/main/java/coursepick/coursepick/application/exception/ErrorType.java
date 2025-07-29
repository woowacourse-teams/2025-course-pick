package coursepick.coursepick.application.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;

import java.util.function.Function;

public enum ErrorType {

    INVALID_LATITUDE_RANGE(
            "위도는 -90 이상, 90 이하이어야 합니다. 입력값=%s",
            IllegalArgumentException::new
    ),
    INVALID_LONGITUDE_RANGE(
            "경도는 -180 이상, 180 미만이어야 합니다. 입력값=%s",
            IllegalArgumentException::new
    ),
    INVALID_NAME_LENGTH(
            "이름은 2-30자 사이이어야 합니다. 입력값=%s",
            IllegalArgumentException::new
    ),
    INVALID_COORDINATE_COUNT(
            "코스는 2개 이상의 좌표로 구성되어야 합니다. 현재 개수=%s",
            IllegalArgumentException::new
    ),
    NOT_CONNECTED_COURSE(
            "코스는 첫 좌표와 끝 좌표가 동일해야 합니다. 첫 좌표=%s, 끝 좌표=%s",
            IllegalArgumentException::new
    ),
    NOT_EXIST_COURSE(
            "코스가 존재하지 않습니다. 코스id=%d",
            EntityNotFoundException::new
    ),
    INVALID_FILE_EXTENSION(
            "파싱할 수 없는 파일 확장자입니다.",
            IllegalArgumentException::new
    ),
    INVALID_ADMIN_TOKEN(
            "올바르지 않은 어드민 토큰값 입니다.",
            SecurityException::new
    ),
    ;

    private final String message;
    private final Function<String, ? extends RuntimeException> exceptionConstructor;

    ErrorType(String message, Function<String, ? extends RuntimeException> exceptionConstructor) {
        this.message = message;
        this.exceptionConstructor = exceptionConstructor;
    }

    @SneakyThrows
    public RuntimeException create(Object... messageArgs) {
        return exceptionConstructor.apply(message(messageArgs));
    }

    public String message(Object... messageArgs) {
        return this.message.formatted(messageArgs);
    }
}
