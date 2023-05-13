package telegrambot.execurors.transaction;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;
import telegrambot.model.Card;
import telegrambot.model.Transaction;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.TransactionTypeEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;
import telegrambot.model.util.drafts.TransactionDraft;
import telegrambot.repository.CardRepository;
import telegrambot.repository.TransactionRepository;
import telegrambot.repository.util.*;

import java.util.Optional;

import static telegrambot.model.enums.CommandEnum.*;
import static telegrambot.model.enums.StateEnum.NO_STATE;

@AllArgsConstructor
@Component
public class ConfirmCreateTransactionExecutor extends AbstractCommandExecutor {

    private final TransactionDraftRepository transactionDraftRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;
    private static final String THIS_CMD = CREATE_TRANSACTION_CONFIRM_COMMAND.getCommand();
    private final CardRepository cardRepository;

    @Transactional
    @Override
    public void exec() {
        CurrentCondition currentCondition = currentConditionRepository.getCurrentCondition();
        String currentCommandName = currentCondition.getCommand().getName();

        if (currentCommandName.equals(CREATE_TRANSACTION_COMMAND.getCommand())) {
            confirmTransaction();
        } else {
            UserDataContextHolder.getFacade()
                    .setText("You can not confirm something from command '" + currentCommandName + "'");
        }
    }

    private void confirmTransaction() {
        Command baseCommand = commandRepository.findByName(START_COMMAND.getCommand());
        State baseState = stateRepository.findByName(NO_STATE.getState());
        Optional<TransactionDraft> draft = Optional.ofNullable(transactionDraftRepository.getFirstDraft());

        Transaction transactionToSave = null;

        if (draft.isEmpty()) {
            processStartCreateTransaction(baseCommand, baseState);
            return;
        }

        DraftStatus draftStatus = draft.get().getStatus();
        if (draftStatus.equals(DraftStatus.BUILT) || draftStatus.equals(DraftStatus.SAVING)) {
            transactionDraftRepository.updateStatus(DraftStatus.SAVING.name());
            transactionToSave = transactionRepository.save(Transaction.builder()
                    .card(draft.get().getCard())
                    .transactionType(draft.get().getType())
                    .amount(draft.get().getAmount())
                    .build());
            Card changingCard = cardRepository.getByName(transactionToSave.getCard().getName());

            if (transactionToSave.getTransactionType().equals(TransactionTypeEnum.INCOME)) {
                cardRepository.updateBalanceByName(changingCard.getBalance().add(transactionToSave.getAmount())
                        , changingCard.getName());
            } else {
                cardRepository.updateBalanceByName(changingCard.getBalance().subtract(transactionToSave.getAmount())
                        , changingCard.getName());
            }
        }

        if (transactionToSave == null) {
            processErrorCreation();
            return;
        }

        cleanAllData();
        currentConditionRepository.updateCommandAndState(baseCommand.getId(), baseState.getId());

        processFinish(transactionToSave);
    }

    private void processFinish(Transaction transactionToSave) {
        UserDataContextHolder.getFacade()
                .setText("Transaction on '" + transactionToSave.getAmount() + "' UAH successfully saved.\nGood luck!")
                .addButtons(getGlobalCommands());
    }

    private void processErrorCreation() {
        UserDataContextHolder.getFacade()
                .setText("Something gone wrong....")
                .addButtons(CREATE_TRANSACTION_CONFIRM_COMMAND)
                .addStartButton();
    }

    private void processStartCreateTransaction(Command command, State state) {
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        UserDataContextHolder.getFacade()
                .setText("Seems you have not started creating transaction.")
                .addButtons(getGlobalCommands());
    }

    @Override
    public boolean isSystemExecutor() {
        return false;
    }

    @Override
    public boolean canExec() {
        return UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        transactionDraftRepository.deleteAll();
        msgFromStateHistoryRepository.deleteAll();

        return msgFromStateHistoryRepository.findLast() == null && transactionDraftRepository.getFirstDraft() == null;
    }
}
