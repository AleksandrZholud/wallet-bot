package telegrambot.execurors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.service.current_condition.CurrentConditionService;
import telegrambot.service.state_history.MsgFromStateHistoryService;

import static telegrambot.model.enums.CommandEnum.START_COMMAND;
import static telegrambot.model.enums.CommandEnum.getGlobalCommands;

@RequiredArgsConstructor
@Component
public class StartExecutor extends AbstractCommandExecutor {
    private final CurrentConditionService currentConditionService;
    private final MsgFromStateHistoryService msgFromStateHistoryService;

    private static final String THIS_CMD = START_COMMAND.getCommand();

    @Override
    public boolean isSystemExecutor() {
        return true;
    }

    @Override
    public void exec() {
        currentConditionService.reset();

        boolean notCleaned = !cleanAllData();
        if (notCleaned) {
            throw new IllegalStateException("Eternal server error: cannot clear all old useless data.");
        }

        UserDataContextHolder.getFacade()
                .setText("Greetings, "
                        + UserDataContextHolder.getSenderName()
                        + "!\nChoose your destiny...:")
                .addButtons(getGlobalCommands());
    }

    @Override
    public boolean canExec() {
        return UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        var handlers = AbstractCommandExecutor.getAllChildEntities();
        handlers.remove(this);

        for (AbstractCommandExecutor handler : handlers) {
            if (!handler.cleanAllData()) {
                return false;
            }
        }

        msgFromStateHistoryService.deleteAll();

        return msgFromStateHistoryService.isEmpty();
    }
}