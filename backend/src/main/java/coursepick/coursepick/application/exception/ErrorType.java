package coursepick.coursepick.application.exception;

import java.util.NoSuchElementException;
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
            NoSuchElementException::new
    ),
    NOT_EXIST_REVIEW(
            "리뷰가 존재하지 않습니다. 리뷰id=%s",
            NoSuchElementException::new
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
    INVALID_ADMIN_PASSWORD(
            "잘못된 관리자 비밀번호입니다.",
            SecurityException::new
    ),
    NOT_FOUND_NOTICE(
            "존재하지 않는 공지 사항입니다. 공지 사항id=%s",
            NoSuchElementException::new
    ),
    AUTHENTICATION_FAIL(
            "인증에 실패하였습니다.",
            UnauthorizedException::new
    ),
    QUERY_TIMEOUT(
            "쿼리 실행에 제한된 시간을 초과했습니다.",
            QueryTimeoutException::new
    ),
    COMPRESS_FAIL(
            "압축에 실패했습니다. 원인=%s",
            RuntimeException::new
    ),
    DECOMPRESS_FAIL(
            "압축 해제에 실패했습니다. 원인=%s",
            RuntimeException::new
    ),
    INVALID_COMPRESS_DATA(
            "압축 및 해제할 데이터가 없습니다.",
            IllegalArgumentException::new
    ),
    INVALID_REVIEW_CONTENT_LENGTH(
            "리뷰 내용은 1자 이상 500자 이하여야 합니다. 입력 길이=%s",
            IllegalArgumentException::new
    ),
    NOT_EXIST_USER(
            "유저가 존재하지 않습니다. 유저id=%s",
            NoSuchElementException::new
    ),
    ALREADY_REPORTED_COURSE(
            "이미 신고한 코스입니다. 코스id=%s, 유저id=%s",
            IllegalArgumentException::new
    ),
    ALREADY_REPORTED_REVIEW(
            "이미 신고한 리뷰입니다. 리뷰id=%s, 유저id=%s",
            IllegalArgumentException::new
    ),
    ALREADY_REVIEWED_COURSE(
            "이미 해당 코스에 리뷰를 작성했습니다. 코스id=%s, 유저id=%s",
            IllegalArgumentException::new
    ),
    DUPLICATED_COURSE_NAME(
            "'%s'은(는) 이미 존재하는 코스 이름입니다.",
            IllegalStateException::new
    ),
    INVALID_REVIEW_RATING(
            "리뷰 평점은 1이상 5이하여야 합니다. 입력값=%s",
            IllegalArgumentException::new
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

    /**
     * API 문서(Swagger) 생성 시, 발생 가능한 에러의 HTTP 상태 코드(400, 404 등)를
     * 매핑하기 위해 예외 클래스 타입을 가져오는 용도로 사용됩니다.
     * ({@link coursepick.coursepick.presentation.api.OpenApiConfig} 참조)
     */
    public Class<? extends RuntimeException> getExceptionClass() {
        return exceptionConstructor.apply("").getClass();
    }

    /**
     * API 문서(Swagger)에 표시할 에러 메시지를 생성합니다.
     * %s 파라미터를 "{입력값}"으로 치환하여 사람이 읽기 쉬운 형태로 변환합니다.
     */
    public String getMessageForApiDoc() {
        return this.message.replace("%s", "{입력값}");
    }
}
