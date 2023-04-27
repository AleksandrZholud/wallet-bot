package telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.config.telegram.BotConfig;
import telegrambot.handlers.AbstractCmdHandler;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.service.card.CardServiceImpl;

@Slf4j
@Component
public class WalletBot extends TelegramLongPollingBot {

    public static final String ERROR_EMPTY_MESSAGE_FOUND = "Error: Cannot understand an empty command!";
    private final BotConfig botConfig;
    private final CardServiceImpl cardService;
    private final CurrentConditionRepository currentConditionRepository;

    public WalletBot(BotConfig botConfig, CardServiceImpl cardService,
                     CurrentConditionRepository currentConditionRepository) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.cardService = cardService;
        this.currentConditionRepository = currentConditionRepository;
    }

    private SendMessage main(Update update, SendMessage sendMessage) throws IllegalAccessException {
        //All logic of TelegramBot is here ↓
        //////////////////////////////////////////////////////////////////////////

        if (update.getMessage().getText().startsWith("/")) {
            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage(update)) {
                    sendMessage = handler.processMessage(update);
                    return sendMessage;
                }
            }
        } else {
            var currentCommand = currentConditionRepository.getFirst().getCommand();
            var temp = update.getMessage().getText();
            update.getMessage().setText(currentCommand.getName());

            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage(update)) {
                    update.getMessage().setText(temp);
                    sendMessage = handler.processMessage(update);
                    return sendMessage;
                }
            }
        }
        return sendMessage;

        //////////////////////////////////////////////////////////////////////////
        //All logic of TelegramBot is here ↑
    }

    @Override
    //doNotModify this Method
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Uaschpie unrecognized command!")
                    .build();
            try {
                sendMessage = main(update, sendMessage);
                sendOutput(update, sendMessage, false);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendMessage.setText(e.getMessage());
                sendOutput(update, sendMessage, true);
            }
        } else {
            sendOutput(update, ERROR_EMPTY_MESSAGE_FOUND, true);
        }
    }

    private void sendOutput(Update update, String errorMsg, boolean isErrorMessage) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(errorMsg)
                .build();
        sendOutput(update, sendMessage, isErrorMessage);
    }

    public void sendOutput(Update update, SendMessage sendMessage, boolean isErrorMessage) {
        SendMessage message = prepareMessage(update, sendMessage, isErrorMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private SendMessage prepareMessage(Update update, SendMessage message, boolean isErrorMessage) {

        message.setText(validateOutputMessage(message.getText()));
        message.setChatId(update.getMessage().getChatId());
        message.setReplyToMessageId(isErrorMessage ? null : update.getMessage().getMessageId());

        return message;
    }

    private String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
}
