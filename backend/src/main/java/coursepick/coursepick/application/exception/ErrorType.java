package coursepick.coursepick.application.exception;

public enum ErrorType {

    INVALID_LONGITUDE_RANGE("경도는 -180 이상, 180 미만이어야 합니다. 입력값=%s"),
    INVALID_LATITUDE_RANGE("위도는 -90 이상, 90 이하이어야 합니다. 입력값=%s"),
    INVALID_NAME_LENGTH("이름은 2-30자 사이이어야 합니다. 입력값=%s"),
    INVALID_COORDINATE_COUNT("코스는 2개 이상의 좌표로 구성되어야 합니다. 현재 개수=%s"),
    NOT_CONNECTED_COURSE("코스는 첫 좌표와 끝 좌표가 동일해야 합니다. 첫 좌표=%s, 끝 좌표=%s"),
    ;

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String message(Object... args) {
        return message.formatted(args);
    }
}
