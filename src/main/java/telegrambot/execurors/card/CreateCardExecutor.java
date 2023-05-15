package telegrambot.execurors.card;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.model.util.drafts.CardDraft;
import telegrambot.repository.util.CommandRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.MsgFromStateHistoryRepository;
import telegrambot.repository.util.StateRepository;
import telegrambot.service.carddraft.CardDraftService;
import telegrambot.service.command.CommandService;
import telegrambot.service.msgfromstatehistory.MsgFromStateHistoryService;
import telegrambot.service.state.StateService;

import java.math.BigDecimal;

import static telegrambot.model.enums.CommandEnum.CREATE_CARD_COMMAND;
import static telegrambot.model.enums.CommandEnum.CREATE_CARD_CONFIRM_COMMAND;
import static telegrambot.model.enums.StateEnum.*;

@AllArgsConstructor
@Component

//TODO: HERE!
public class CreateCardExecutor extends AbstractCommandExecutor {
    private final CardDraftService cardDraftService;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandService commandService;
    private final StateService stateService;
    private final MsgFromStateHistoryService msgFromStateHistoryService;
    private static final String THIS_CMD = CREATE_CARD_COMMAND.getCommand();

    @Override
    public boolean isSystemExecutor() {
        return false;
    }

    @Override
    public void exec() {

        if (UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD)) {
            cardDraftService.deleteAll();
            currentConditionRepository.updateCommandAndState(3L, 1L);
            msgFromStateHistoryService.deleteAll();
        }

        CurrentCondition currentCondition = currentConditionRepository.getCurrentCondition();

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
        var command = commandService.findByName(THIS_CMD);
        var state = stateService.findByName(SET_NAME.getState());

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftService.claenupAndCreateFirst();

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
        var command = commandService.findByName(THIS_CMD);
        var state = stateService.findByName(SET_BALANCE.getState());
        var draftName = UserDataContextHolder.getInputtedTextCommand();

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

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
        var command = commandService.findByName(THIS_CMD);
        var state = stateService.findByName(SET_BALANCE.getState());
        long longValueOfInput = tryGetLongValue();
        var draftBalance = BigDecimal.valueOf(longValueOfInput);

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        CardDraft cd = cardDraftService.updateBalanceAndGetEntity(draftBalance);

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
        var currentCommandName = currentConditionRepository.getCurrentCondition().getCommand().getName();
        var message = UserDataContextHolder.getInputtedTextCommand();

        return message.equals(THIS_CMD) || currentCommandName.equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        cardDraftService.deleteAll();
        return cardDraftService.getFirstDraft() == null;
    }
}