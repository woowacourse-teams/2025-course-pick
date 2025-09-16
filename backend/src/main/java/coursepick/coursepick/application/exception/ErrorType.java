package coursepick.coursepick.application.exception;

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
    INVALID_DUPLICATE_COORDINATE_ONLY_START_END(
            "시작과 끝 좌표만 존재할 때 둘은 중복될 수 없습니다.",
            IllegalArgumentException::new
    ),
    NOT_EXIST_COURSE(
            "코스가 존재하지 않습니다. 코스id=%s",
            NotFoundException::new
    ),
    INVALID_FILE_EXTENSION(
            "파싱할 수 없는 파일 확장자입니다.",
            IllegalArgumentException::new
    ),
    FILE_PARSING_FAIL(
            "파일 파싱에 실패했습니다. 이유=%s",
            IllegalArgumentException::new
    ),
    INVALID_ADMIN_TOKEN(
            "올바르지 않은 어드민 토큰값 입니다.",
            SecurityException::new
    ),
    INVALID_RATIO_BASE(
            "0을 기준으로 비율을 계산할 수 없습니다.",
            IllegalArgumentException::new
    ),
    NOT_EXIST_TOKEN(
            "토큰이 존재하지 않습니다.",
            SecurityException::new
    ),
    TOKEN_EXPIRED(
            "만료된 토큰입니다.",
            SecurityException::new
    ),
    TOKEN_INVALID(
            "잘못된 토큰입니다.",
            SecurityException::new
    ),
    LOGIN_FAIL(
            "로그인에 실패했습니다.",
            SecurityException::new
    ),
    INVALID_PASSWORD(
            "잘못된 비밀번호입니다.",
            SecurityException::new
    );

    private final String message;
    private final Function<String, ? extends RuntimeException> exceptionConstructor;

    ErrorType(String message, Function<String, ? extends RuntimeException> exceptionConstructor) {
        this.message = message;
        this.exceptionConstructor = exceptionConstructor;
    }

    public RuntimeException create(Object... messageArgs) {
        return exceptionConstructor.apply(message(messageArgs));
    }

    public String message(Object... messageArgs) {
        return this.message.formatted(messageArgs);
    }
}
