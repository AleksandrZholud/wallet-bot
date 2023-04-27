package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;

@AllArgsConstructor
@Component
public class BackCmdHandler  extends AbstractCmdHandler{
    private static final String THIS_CMD = "/back";

    @Override
    public SendMessage processMessage() {
        return null;
    }

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }
}
