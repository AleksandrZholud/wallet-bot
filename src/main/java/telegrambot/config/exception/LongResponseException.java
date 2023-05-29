package telegrambot.config.exception;

public class LongResponseException extends RuntimeException {

    public LongResponseException() {
        super();
    }

    public LongResponseException(String message) {
        super(message);
    }

    public LongResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LongResponseException(Throwable cause) {
        super(cause);
    }

    protected LongResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
