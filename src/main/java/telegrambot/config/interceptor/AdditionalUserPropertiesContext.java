package telegrambot.config.interceptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.model.SendMessageFacade;

@Setter
@Getter
@AllArgsConstructor
public class AdditionalUserPropertiesContext {
    private AdditionalUserPropertiesContext() {
    }

    public AdditionalUserPropertiesContext(Update update){
        this.update = update;
        this.sendMessageFacade = new SendMessageFacade(update.getMessage().getChatId());
    }

    private Update update;
    private SendMessageFacade sendMessageFacade;
}