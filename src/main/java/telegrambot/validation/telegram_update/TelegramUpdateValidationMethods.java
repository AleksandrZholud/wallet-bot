package telegrambot.validation.telegram_update;

import org.springframework.validation.Errors;

public class TelegramUpdateValidationMethods {

    private static final String FIELD_DATE = "Date";

    public void responseTimeTooLong(Integer msgUnixTimeDate, Errors errors) {
        if (!errors.hasFieldErrors(FIELD_DATE)) {
            // TODO: 29.05.2023 test response time format to fit System.currentTimeMillis()
            var timeDiff = System.currentTimeMillis() - msgUnixTimeDate;
            if (timeDiff > 43200000L) {
                Object[] errorArgs = {FIELD_DATE};
                errors.rejectValue(FIELD_DATE, "telegram_update.field.Date.tooOld", errorArgs, "");
            }
        }

    }
}
