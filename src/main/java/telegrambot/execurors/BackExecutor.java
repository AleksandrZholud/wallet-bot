package telegrambot.execurors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.model.util.State;
import telegrambot.service.current_condition.CurrentConditionService;
import telegrambot.service.state_history.MsgFromStateHistoryService;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;

@Slf4j
@RequiredArgsConstructor
@Component
public class BackExecutor extends AbstractCommandExecutor {
    private final CurrentConditionService currentConditionService;
    private final MsgFromStateHistoryService msgFromStateHistoryService;

    private static final String THIS_CMD = GO_BACK_COMMAND.getCommand();

    @Override
    public boolean isSystemExecutor() {
        return true;
    }

    @Override
    public void exec() {
        try {
            goBack();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            AbstractCommandExecutor.getSpecificChild(StartExecutor.class).exec();
        }
    }

    private void goBack() {

        String previousMessage = msgFromStateHistoryService.getPreLast().getMessage();
        State previousState = currentConditionService.getPreviousState();

        if (previousMessage == null || previousState == null) {
            AbstractCommandExecutor.getSpecificChild(StartExecutor.class).exec();
        } else {
            msgFromStateHistoryService.removeLast();
            currentConditionService.updateState(previousState);
            UserDataContextHolder.getFacade()
                    .setText(previousMessage)
                    .addBackButton()
                    .addStartButton();
        }
    }

    @Override
    public boolean canExec() {
        return UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        return true;
    }
}