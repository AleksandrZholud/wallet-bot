package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

import static telegrambot.model.enums.CommandEnum.CREATE_CARD_COMMAND;

@AllArgsConstructor
@Component
public class StartCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = "/start";
    private final CurrentConditionRepository currentConditionRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public void processMessage() {
        AdditionalUserPropertiesContextHolder.getFacade()
                .setText("Greetings, "
                        + AdditionalUserPropertiesContextHolder.getUpdate().getMessage().getChat().getFirstName()
                        + "!\nChoose your destiny...:")
                .addButtons(false, false, CREATE_CARD_COMMAND);

        currentConditionRepository.updateCommandAndState(1L, 1L);
        cleanAllData();
    }

    @Override
    public boolean canProcessMessage() {
        return AdditionalUserPropertiesContextHolder.getUpdate().getMessage().getText().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        var handlers = AbstractCmdHandler.getAllChildEntities();
        handlers.remove(this);

        for (AbstractCmdHandler handler : handlers) {
            if (!handler.cleanAllData()) {
                return false;
            }
        }

        msgFromStateHistoryRepository.deleteAll();

        return msgFromStateHistoryRepository.findLast() == null;
    }
}