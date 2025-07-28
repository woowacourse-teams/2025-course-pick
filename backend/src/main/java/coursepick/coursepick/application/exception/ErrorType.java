package coursepick.coursepick.application.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;

public enum ErrorType {

    INVALID_LATITUDE_RANGE(
            "위도는 -90 이상, 90 이하이어야 합니다. 입력값=%s",
            IllegalArgumentException.class
    ),
    INVALID_LONGITUDE_RANGE(
            "경도는 -180 이상, 180 미만이어야 합니다. 입력값=%s",
            IllegalArgumentException.class
    ),
    INVALID_NAME_LENGTH(
            "이름은 2-30자 사이이어야 합니다. 입력값=%s",
            IllegalArgumentException.class
    ),
    INVALID_COORDINATE_COUNT(
            "코스는 2개 이상의 좌표로 구성되어야 합니다. 현재 개수=%s",
            IllegalArgumentException.class
    ),
    NOT_CONNECTED_COURSE(
            "코스는 첫 좌표와 끝 좌표가 동일해야 합니다. 첫 좌표=%s, 끝 좌표=%s",
            IllegalArgumentException.class
    ),
    NOT_EXIST_COURSE(
            "코스가 존재하지 않습니다. 코스id=%d",
            EntityNotFoundException.class
    ),
    INVALID_FILE_EXTENSION(
            "파싱할 수 없는 파일 확장자입니다.",
            IllegalArgumentException.class
    ),
    INVALID_ADMIN_TOKEN(
            "올바르지 않은 어드민 토큰값 입니다.",
            SecurityException.class
    );

    private final String message;
    private final Class<? extends RuntimeException> exceptionType;

    ErrorType(String message, Class<? extends RuntimeException> exceptionType) {
        this.message = message;
        this.exceptionType = exceptionType;
    }

    @SneakyThrows
    public RuntimeException create(Object... messageArgs) {
        return exceptionType.getConstructor(String.class).newInstance(message(messageArgs));
    }

    public String message(Object... messageArgs) {
        return this.message.formatted(messageArgs);
    }
}
