package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.repository.TransactionRepository;
import telegrambot.repository.util.*;

import static telegrambot.model.enums.CommandEnum.CREATE_TRANSACTION_COMMAND;

@AllArgsConstructor
@Component
public class ConfirmCreateTransactionExecutor extends AbstractCommandExecutor {

    private final TransactionDraftRepository transactionDraftRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;
    private static final String THIS_CMD = CREATE_TRANSACTION_COMMAND.getCommand();

    @Override
    public void processMessage() throws IllegalAccessException {

    }

    @Override
    public boolean isSystemHandler() {
        return false;
    }

    @Override
    public boolean canProcessMessage() {
        return UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        transactionDraftRepository.deleteAll();
        msgFromStateHistoryRepository.deleteAll();

        return msgFromStateHistoryRepository.findLast() == null && transactionDraftRepository.getFirstDraft() == null;
    }
}
