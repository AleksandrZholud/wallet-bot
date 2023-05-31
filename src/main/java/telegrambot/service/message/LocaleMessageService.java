package telegrambot.service.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
//@RequiredArgsConstructor
public class LocaleMessageService implements MessageService {

    private final MessageSource messageSource;

    private final Locale locale;

    public LocaleMessageService(MessageSource messageSource,@Value("${server.locale}") Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }


    @Override
    public String getMessage(String messageCode) {
        return messageSource.getMessage(messageCode, null, this.locale);
    }

    @Override
    public String getMessage(String messageCode, Locale locale) {
        return messageSource.getMessage(messageCode, null, locale);
    }

    @Override
    public String getMessage(String messageCode, Object... args) {
        return messageSource.getMessage(messageCode, args, this.locale);
    }

    @Override
    public String getMessage(String messageCode, Locale locale, Object... args) {
        return messageSource.getMessage(messageCode, args, locale);
    }
}