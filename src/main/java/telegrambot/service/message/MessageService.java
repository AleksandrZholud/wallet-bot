package telegrambot.service.message;

import java.util.Locale;

public interface MessageService {

    String getMessage(String messageCode);

    String getMessage(String messageCode, Locale locale);

    String getMessage(String messageCode, Object... args);

    String getMessage(String messageCode, Locale locale, Object... args);
}
