package coursepick.coursepick.application.exception;

public class InvalidArgumentException extends ApplicationException {

    public InvalidArgumentException(ErrorType type) {
        super(type);
    }
}
