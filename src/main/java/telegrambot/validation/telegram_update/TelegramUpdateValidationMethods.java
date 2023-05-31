package telegrambot.validation.telegram_update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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


            log.info(getFormattedTimeFromSeconds(delay) + " passed from user response");
            if (delay > responseDelay) {
                log.warn("Response is irrelevant for now");
                Object[] errorArgs = {FIELD_MESSAGE};
                errors.rejectValue(FIELD_MESSAGE, "telegram_update.field.message.date.tooOld", errorArgs, "");
            }
        }
    }

    private String getFormattedTimeFromSeconds(long seconds) {
        long h = seconds / 3600;
        long m = (seconds - (h * 3600)) / 60;
        long s = seconds - (h * 3600) - (m * 60);
        List<String> resultList = new ArrayList<>();

        if (h != 0) resultList.add(h + " hours");
        if (m != 0) resultList.add(m + " minutes");
        if (s != 0) resultList.add(s + " seconds");

        return resultList.isEmpty() ? "0 seconds" : String.join(", ", resultList);
    }
}
