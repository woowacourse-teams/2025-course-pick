package coursepick.coursepick.application.exception;

public abstract class ApplicationException extends RuntimeException {

    private final ErrorType errorType;

    protected ApplicationException(ErrorType errorType) {
        super(errorType.message());
        this.errorType = errorType;
    }

    public ErrorType errorType() {
        return errorType;
    }
}
