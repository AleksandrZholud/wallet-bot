package telegrambot.execurors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.service.currentcondition.CurrentConditionService;
import telegrambot.service.msgfromstatehistory.MsgFromStateHistoryService;

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

        String previousMessage = msgFromStateHistoryService.findPreLast();
        long previousStateId = currentConditionService.getPreviousStateId();

        if (previousMessage == null || previousStateId == 0) {
            AbstractCommandExecutor.getSpecificChild(StartExecutor.class).exec();
        } else {
            msgFromStateHistoryService.removeLast();
            currentConditionService.updateState(previousStateId);
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