package telegrambot.service.validation;

import telegrambot.config.exception.TelegramUpdateValidationException;

public interface ValidationService {
    void validate(Object obj) throws TelegramUpdateValidationException;
}
