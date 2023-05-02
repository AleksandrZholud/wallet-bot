package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.*;

import java.math.BigDecimal;

import static telegrambot.model.enums.CommandEnum.CREATE_CARD_COMMAND;
import static telegrambot.model.enums.CommandEnum.CREATE_CARD_CONFIRM_COMMAND;
import static telegrambot.model.enums.StateEnum.*;

@AllArgsConstructor
@Component
public class CreateCardCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = CREATE_CARD_COMMAND.getCommand();
    private final CardDraftRepository cardDraftRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public void processMessage() {

        if (AdditionalUserPropertiesContextHolder.getInputtedTextComand().equals(THIS_CMD)) {
            cardDraftRepository.deleteAll();
            currentConditionRepository.updateCommandAndState(3L, 1L);
            msgFromStateHistoryRepository.deleteAll();
        }

        CurrentCondition currentCondition = currentConditionRepository.getFirst();

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
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName(SET_NAME.getState());

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.deleteAll();
        cardDraftRepository.createFirstDraft();

        String enterCardNameMsg = "Enter Card name:";

        msgFromStateHistoryRepository.save(
                MsgFromStateHistory.builder()
                        .message(enterCardNameMsg)
                        .build());

        AdditionalUserPropertiesContextHolder.getFacade()
                .setText(enterCardNameMsg);
    }

    private void doSetName() {
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName(SET_BALANCE.getState());
        var draftName = AdditionalUserPropertiesContextHolder.getInputtedTextComand();

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.updateName(draftName);

        String setBalanceMsg = "Card name: '" + draftName + "'.\nEnter start balance:";
        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(setBalanceMsg)
                .build());

        AdditionalUserPropertiesContextHolder.getFacade()
                .setText(setBalanceMsg)
                .addBackButton()
                .addStartButton();
    }

    private void doSetBalance() {
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName(CONFIRMATION.getState());
        long longValueOfInput = tryGetLongValue();
        var draftBalance = BigDecimal.valueOf(longValueOfInput);

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.updateBalance(draftBalance);
        cardDraftRepository.updateStatus(DRAFT_STATUS.BUILT.name());
        var cd = cardDraftRepository.getFirstDraft();

        String text = "Confirm your Card:"
                + "\nCard name   : '" + cd.getName() + "'"
                + "\nCard balance: " + cd.getBalance();

        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(text)
                .build());

        AdditionalUserPropertiesContextHolder.getFacade()
                .setText(text)
                .addButtons(CREATE_CARD_CONFIRM_COMMAND)
                .addBackButton()
                .addStartButton();
    }

    private long tryGetLongValue() {
        long longValueOfInput;
        try {
            longValueOfInput = Long.parseLong(AdditionalUserPropertiesContextHolder.getInputtedTextComand());
        } catch (Exception e) {
            throw new IllegalStateException("Error: You tried input a text value for a number.");
        }
        return longValueOfInput;
    }

    @Override
    public boolean canProcessMessage() {
        var currentCommandName = currentConditionRepository.getFirst().getCommand().getName();
        var message = AdditionalUserPropertiesContextHolder.getInputtedTextComand();

        return message.startsWith("/") ?
                message.equals(THIS_CMD) : currentCommandName.equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        cardDraftRepository.deleteAll();
        return cardDraftRepository.getFirstDraft() == null;
    }
}