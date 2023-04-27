package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.WalletBot;

@AllArgsConstructor
@Component
public class BackCmdHandler  extends AbstractCmdHandler{
    @Autowired
    private final WalletBot walletBot;

    private static final String THIS_CMD = "/back";


    @Override
    public SendMessage processMessage(Update update) {
        return null;
    }

    @Override
    public boolean canProcessMessage(Update update) {
        return update.getMessage().getText().equals(THIS_CMD);
    }
}
