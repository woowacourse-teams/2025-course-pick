package coursepick.coursepick.application.exception;

public enum ErrorType {

    INVALID_LONGITUDE_RANGE("경도의 범위를 넘을 수 없습니다."),
    INVALID_LATITUDE_RANGE("위도의 범위를 넘을 수 없습니다."),
    INVALID_NAME_LENGTH("이름은 2-30자 사이여야 합니다."),
    INVALID_COORDINATE_COUNT("코스는 2개 이상의 좌표로 구성되어야 합니다."),
    NOT_CONNECTED_COURSE("코스는 첫 좌표와 끝 좌표가 동일해야 합니다."),
    ;

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
