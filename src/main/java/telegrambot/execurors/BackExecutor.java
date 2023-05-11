package telegrambot.execurors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;

@Slf4j
@RequiredArgsConstructor
@Component
public class BackExecutor extends AbstractCommandExecutor {
    private final CurrentConditionRepository currentConditionRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    private static final String THIS_CMD = GO_BACK_COMMAND.getCommand();

    @Override
    public boolean isSystemHandler() {
        return true;
    }

    @Override
    public void processMessage() {
        try {
            goBack();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            AbstractCommandExecutor.getSpecificChild(StartExecutor.class).processMessage();
        }
    }

    private void goBack() {

        String previousMessage = msgFromStateHistoryRepository.findPreLast();
        long previousStateId = currentConditionRepository.getPreviousStateId();

        if (previousMessage == null || previousStateId == 0) {
            AbstractCommandExecutor.getSpecificChild(StartExecutor.class).processMessage();
        } else {
            msgFromStateHistoryRepository.removeLast();
            currentConditionRepository.updateState(previousStateId);
            UserDataContextHolder.getFacade()
                    .setText(previousMessage)
                    .addBackButton()
                    .addStartButton();
        }
    }

    @Override
    public boolean canProcessMessage() {
        return UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        return true;
    }
}