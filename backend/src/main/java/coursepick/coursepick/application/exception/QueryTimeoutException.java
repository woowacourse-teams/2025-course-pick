package coursepick.coursepick.application.exception;

public class QueryTimeoutException extends RuntimeException {

    public QueryTimeoutException(String message) {
        super(message);
    }
}
