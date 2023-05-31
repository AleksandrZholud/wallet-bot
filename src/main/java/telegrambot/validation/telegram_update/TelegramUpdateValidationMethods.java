package telegrambot.validation.telegram_update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateValidationMethods {

    private static final String FIELD_MESSAGE = "message";

    public void responseTimeTooLong(Integer msgUnixTimeDate, Errors errors) {
        if (!errors.hasFieldErrors(FIELD_MESSAGE)) {
            var r = Instant.now().getEpochSecond() - msgUnixTimeDate.longValue();
            log.info("Response time is "+r+" sec");
            if (r > 3) {
                Object[] errorArgs = {FIELD_MESSAGE};
                errors.rejectValue(FIELD_MESSAGE, "telegram_update.field.message.date.tooOld", errorArgs, "");
            }
        }
    }
}
