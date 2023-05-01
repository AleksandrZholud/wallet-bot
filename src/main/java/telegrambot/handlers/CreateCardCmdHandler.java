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

        if (AdditionalUserPropertiesContextHolder.getUpdate().getMessage().getText().equals(THIS_CMD)) {
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
        var draftName = AdditionalUserPropertiesContextHolder.getFacade().getText();

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.updateName(draftName);

        String setBalanceMsg = "Enter start balance for Card '" + draftName + "':";
        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(setBalanceMsg)
                .build());

        AdditionalUserPropertiesContextHolder.getFacade()
                .setText(setBalanceMsg)
                .addButtons(true, true);
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
                + "\nName: '" + cd.getName() + "'"
                + "\nBalance: " + cd.getBalance();

        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(text)
                .build());

        AdditionalUserPropertiesContextHolder.getFacade()
                .setText(text)
                .addButtons(true, CREATE_CARD_CONFIRM_COMMAND);
    }

    private long tryGetLongValue() {
        long longValueOfInput;
        try {
            longValueOfInput = Long.parseLong(AdditionalUserPropertiesContextHolder.getFacade().getText());
        }
        catch (Exception e){
            throw new IllegalStateException("Error: You tried input a text value for a number.");
        }
        return longValueOfInput;
    }

    @Override
    public boolean canProcessMessage() {
        var currentCommandName = currentConditionRepository.getFirst().getCommand().getName();
        var message = AdditionalUserPropertiesContextHolder.getUpdate().getMessage().getText();

        return message.startsWith("/") ?
                message.equals(THIS_CMD) : currentCommandName.equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        cardDraftRepository.deleteAll();
        return cardDraftRepository.getFirstDraft() == null;
    }
}