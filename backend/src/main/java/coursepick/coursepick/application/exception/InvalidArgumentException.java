package coursepick.coursepick.application.exception;

public class InvalidArgumentException extends RuntimeException {
    private final ErrorType errorType;

    public InvalidArgumentException(ErrorType type) {
        super(type.message());
        this.errorType = type;
    }
}
