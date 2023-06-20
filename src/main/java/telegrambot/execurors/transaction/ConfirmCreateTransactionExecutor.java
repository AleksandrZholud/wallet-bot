package telegrambot.execurors.transaction;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;
import telegrambot.model.drafts.TransactionDraft;
import telegrambot.model.entity.Card;
import telegrambot.model.entity.Transaction;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.TransactionTypeEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;
import telegrambot.service.card.CardService;
import telegrambot.service.command.CommandService;
import telegrambot.service.current_condition.CurrentConditionService;
import telegrambot.service.state.StateService;
import telegrambot.service.state_history.MsgFromStateHistoryService;
import telegrambot.service.transaction.TransactionService;
import telegrambot.service.transaction_draft.TransactionDraftService;

import java.util.Optional;

import static telegrambot.model.enums.CommandEnum.*;
import static telegrambot.model.enums.StateEnum.NO_STATE;

@AllArgsConstructor
@Component
public class ConfirmCreateTransactionExecutor extends AbstractCommandExecutor {

    private final TransactionDraftService transactionDraftService;
    private final TransactionService transactionService;
    private final CurrentConditionService currentConditionService;
    private final CommandService commandService;
    private final StateService stateService;
    private final MsgFromStateHistoryService msgFromStateHistoryService;
    private final CardService cardService;
    private static final String THIS_CMD = CREATE_TRANSACTION_CONFIRM_COMMAND.getCommand();

    @Transactional
    @Override
    public void exec() {
        CurrentCondition currentCondition = currentConditionService.getCurrentCondition();
        String currentCommandName = currentCondition.getCommand().getName();

        if (currentCommandName.equals(CREATE_TRANSACTION_COMMAND.getCommand())) {
            confirmTransaction();
        } else {
            UserDataContextHolder.getFacade()
                    .setText("You can not confirm something from command '" + currentCommandName + "'");
        }
    }

    private void confirmTransaction() {
        Command baseCommand = commandService.getByName(START_COMMAND.getCommand());
        State baseState = stateService.findByName(NO_STATE.getState());
        Optional<TransactionDraft> draft = Optional.ofNullable(transactionDraftService.getFirstDraft());

        Transaction transactionToSave = null;

        if (draft.isEmpty()) {
            processStartCreateTransaction(baseCommand, baseState);
            return;
        }

        DraftStatus draftStatus = draft.get().getStatus();
        if (draftStatus.equals(DraftStatus.BUILT) || draftStatus.equals(DraftStatus.SAVING)) {
            transactionDraftService.updateStatus(DraftStatus.SAVING);
            transactionToSave = transactionService.save(Transaction.builder()
                    .card(draft.get().getCard())
                    .transactionType(draft.get().getType())
                    .amount(draft.get().getAmount())
                    .build());
            Card changingCard = cardService.getByName(transactionToSave.getCard().getName());

            if (transactionToSave.getTransactionType().equals(TransactionTypeEnum.INCOME)) {
                cardService.updateBalanceByName(changingCard.getBalance().add(transactionToSave.getAmount())
                        , changingCard.getName());
            } else {
                cardService.updateBalanceByName(changingCard.getBalance().subtract(transactionToSave.getAmount())
                        , changingCard.getName());
            }
        }

        if (transactionToSave == null) {
            processErrorCreation();
            return;
        }

        cleanAllData();
        currentConditionService.updateCommandAndState(baseCommand, baseState);

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
        currentConditionService.updateCommandAndState(command, state);
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
        transactionDraftService.deleteAll();
        msgFromStateHistoryService.deleteAll();

        return msgFromStateHistoryService.isEmpty() && transactionDraftService.isEmpty();
    }
}
