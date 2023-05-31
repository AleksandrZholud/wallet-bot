package telegrambot.validation.telegram_update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateValidationMethods {

    @NotEmpty
    @Value("${user-response-delay-in-seconds}")
    private long responseDelay;
    private static final String FIELD_MESSAGE = "message";

    public void responseTimeTooLong(Integer msgUnixTimeDate, Errors errors) {
        if (!errors.hasFieldErrors(FIELD_MESSAGE)) {
            long delay = Instant.now().getEpochSecond() - msgUnixTimeDate.longValue();

            log.info("Time passed from user response: " + delay + " seconds.");
            if (delay > responseDelay) {
                log.warn("Response is irrelevant for now");
                Object[] errorArgs = {FIELD_MESSAGE};
                errors.rejectValue(FIELD_MESSAGE, "telegram_update.field.message.date.tooOld", errorArgs, "");
            }
        }
    }
}
