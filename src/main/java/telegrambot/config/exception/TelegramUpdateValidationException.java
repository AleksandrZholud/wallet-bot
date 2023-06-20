package telegrambot.config.exception;

public class TelegramUpdateValidationException extends RuntimeException {

    public TelegramUpdateValidationException(String error) {
        super(error);
    }
}
