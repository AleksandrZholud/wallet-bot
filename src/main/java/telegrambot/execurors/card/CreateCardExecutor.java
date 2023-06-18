package telegrambot.execurors.card;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;
import telegrambot.model.drafts.CardDraft;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.service.card.CardService;
import telegrambot.service.card_draft.CardDraftService;
import telegrambot.service.command.CommandService;
import telegrambot.service.current_condition.CurrentConditionService;
import telegrambot.service.state.StateService;
import telegrambot.service.state_history.MsgFromStateHistoryService;

import java.math.BigDecimal;

import static telegrambot.model.enums.CommandEnum.*;
import static telegrambot.model.enums.StateEnum.*;

@AllArgsConstructor
@Component

public class CreateCardExecutor extends AbstractCommandExecutor {
    private final CardDraftService cardDraftService;
    private final CurrentConditionService currentConditionService;
    private final CommandService commandService;
    private final StateService stateService;
    private final MsgFromStateHistoryService msgFromStateHistoryService;
    private final CardService cardService;
    private static final String THIS_CMD = CREATE_CARD_COMMAND.getCommand();

    @Override
    public boolean isSystemExecutor() {
        return false;
    }

    @Override
    public void exec() {

        if (UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD)) {
            cardDraftService.deleteAll();
            currentConditionService.updateCommandAndState(CREATE_CARD_COMMAND, NO_STATE);
            msgFromStateHistoryService.deleteAll();
        }

        CurrentCondition currentCondition = currentConditionService.getCurrentCondition();

        if (currentCondition.getState().getName().equals(NO_STATE.getState())) {
            doCreateCard();
        } else {
            if (currentCondition.getState().getName().equals(SET_NAME.getState())) {
                doSetName();
            } else {
                doSetBalance();
            }
        }
    }

    private void doCreateCard() {
        var command = commandService.getByName(THIS_CMD);
        var state = stateService.findByName(SET_NAME.getState());

        currentConditionService.updateCommandAndState(command, state);

        cardDraftService.createSingleDraft();

        String enterCardNameMsg = "Enter Card name:";

        msgFromStateHistoryService.save(
                MsgFromStateHistory.builder()
                        .message(enterCardNameMsg)
                        .build());

        UserDataContextHolder.getFacade()
                .setText(enterCardNameMsg)
                .addStartButton();
    }

    private void doSetName() {
        var draftName = UserDataContextHolder.getInputtedTextCommand();

        if(cardService.checkIfExistByName(draftName)) {
            UserDataContextHolder.getFacade()
                    .setText("Card with '" + draftName + "' already exist.")
                    .addButtons(CREATE_TRANSACTION_COMMAND, CREATE_CARD_COMMAND);
            return;
        }

        var command = commandService.getByName(THIS_CMD);
        var state = stateService.findByName(SET_BALANCE.getState());

        currentConditionService.updateCommandAndState(command, state);

        cardDraftService.updateName(draftName);

        String setBalanceMsg = "Card name: '" + draftName + "'\n\nEnter start balance:";
        msgFromStateHistoryService.save(MsgFromStateHistory.builder()
                .message(setBalanceMsg)
                .build());

        UserDataContextHolder.getFacade()
                .setText(setBalanceMsg)
                .addBackButton()
                .addStartButton();
    }

    private void doSetBalance() {
        var command = commandService.getByName(THIS_CMD);
        var state = stateService.findByName(SET_BALANCE.getState());
        long longValueOfInput = tryGetLongValue();
        var draftBalance = BigDecimal.valueOf(longValueOfInput);

        currentConditionService.updateCommandAndState(command, state);

        CardDraft cd = cardDraftService.updateBalance(draftBalance);

        String text = "Confirm your Card:\n"
                + "\nCard name   : '" + cd.getName() + "'"
                + "\nCard balance: " + cd.getBalance();

        msgFromStateHistoryService.save(MsgFromStateHistory.builder()
                .message(text)
                .build());

        UserDataContextHolder.getFacade()
                .setText(text)
                .addButtons(CREATE_CARD_CONFIRM_COMMAND)
                .addBackButton()
                .addStartButton();
    }

    private long tryGetLongValue() {
        long longValueOfInput;
        try {
            longValueOfInput = Long.parseLong(UserDataContextHolder.getInputtedTextCommand());
        } catch (Exception e) {
            throw new IllegalStateException("Error: You tried input a text value for a number, try again.");
        }
        return longValueOfInput;
    }

    @Override
    public boolean canExec() {
        var currentCommandName = currentConditionService.getCurrentCondition().getCommand().getName();
        var message = UserDataContextHolder.getInputtedTextCommand();

        return message.equals(THIS_CMD) || currentCommandName.equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        cardDraftService.deleteAll();
        return cardDraftService.isEmpty();
    }
}