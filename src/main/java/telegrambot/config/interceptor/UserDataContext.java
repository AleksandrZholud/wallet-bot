package telegrambot.config.interceptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.model.SendMessageFacade;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class UserDataContext {

    private Update update;
    private SendMessageFacade sendMessageFacade;

    private UserDataContext() {
    }

    public UserDataContext(Update update) {
        this.update = update;
        this.sendMessageFacade = new SendMessageFacade(update.getMessage().getChatId());
    }
}