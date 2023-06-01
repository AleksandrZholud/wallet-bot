package telegrambot.validation.telegram_update;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.validation.AbstractConstraintValidator;

@Component
@RequiredArgsConstructor
public class TelegramUpdateValidator extends AbstractConstraintValidator {

    private final TelegramUpdateValidationMethods methods;
    @Override
    protected void checkSpecialConstraintViolations(Object target, Errors errors) {
        Update updateEntity = (Update) target;

        methods.responseTimeTooLong(updateEntity.getMessage().getDate(), errors);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Update.class.equals(clazz);
    }
}
