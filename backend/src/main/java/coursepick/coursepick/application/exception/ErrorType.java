package coursepick.coursepick.application.exception;

public enum ErrorType {

    INVALID_LONGITUDE_RANGE(""),
    INVALID_LATITUDE_RANGE(""),
    INVALID_NAME_LENGTH(""),
    INVALID_COORDINATE_COUNT(""),
    NOT_CONNECTED_COURSE(""),
    ;

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
