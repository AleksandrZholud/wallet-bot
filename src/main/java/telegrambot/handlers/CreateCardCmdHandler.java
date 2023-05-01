package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.*;
import telegrambot.util.SendMessageUtils;

import java.math.BigDecimal;

import static telegrambot.model.enums.CommandEnum.CREATE_CARD_COMMAND;
import static telegrambot.model.enums.CommandEnum.CREATE_CARD_CONFIRM_COMMAND;

@AllArgsConstructor
@Component
public class CreateCardCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = CREATE_CARD_COMMAND.getCommand();
    public static final String SET_NAME = "setName";
    private final CardDraftRepository cardDraftRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public SendMessage processMessage() {
        SendMessage sendMessage;
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();

        if (update.getMessage().getText().equals(THIS_CMD)) {
            cardDraftRepository.deleteAll();
            currentConditionRepository.updateCommandAndState(3L, 1L);
            msgFromStateHistoryRepository.deleteAll();
        }

        CurrentCondition currentCondition = currentConditionRepository.getFirst();

        if (currentCondition.getState().getName().equals("noState")) {
            sendMessage = doCreateCard();
        } else {
            if (currentCondition.getState().getName().equals(SET_NAME)) {
                sendMessage = doSetName();
            } else {
                sendMessage = doSetBalance();
            }
        }

        return sendMessage;
    }

    private SendMessage doCreateCard() {
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName(SET_NAME);

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.deleteAll();
        cardDraftRepository.createFirstDraft();

        String enterCardNameMsg = "Enter Card name:";
        msgFromStateHistoryRepository.save(
                MsgFromStateHistory.builder()
                .message(enterCardNameMsg)
                .build());

        return SendMessageUtils.getSendMessageWithChatIdAndText(enterCardNameMsg);
    }

    private SendMessage doSetName() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName("setBalance");
        var draftName = update.getMessage().getText();

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.updateName(draftName);

        String setBalanceMsg = "Enter start balance for Card '" + draftName + "':";
        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(setBalanceMsg)
                .build());

        SendMessage sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(setBalanceMsg);
        SendMessageUtils.addButtonsWithStart(sendMessage, true);
        return sendMessage;
    }

    private SendMessage doSetBalance() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName("confirmation");
        var draftBalance = BigDecimal.valueOf(Long.parseLong(update.getMessage().getText()));

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.updateBalance(draftBalance);
        cardDraftRepository.updateStatus(DRAFT_STATUS.BUILT.name());
        var cd = cardDraftRepository.getFirstDraft();

        SendMessage sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(
                "Confirm your Card:"
                        + "\nName: '" + cd.getName() + "'"
                        + "\nBalance: " + cd.getBalance());
        SendMessageUtils.addButtonsWithStart(sendMessage, true, CREATE_CARD_CONFIRM_COMMAND);

        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(sendMessage.getText())
                .build());

        return sendMessage;
    }

    // TODO: 30.04.2023 make method as default in AbstractHandler? Override it only in /back,/start,confirm commands
    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var currentCommandName = currentConditionRepository.getFirst().getCommand().getName();
        var message = update.getMessage().getText();

        return message.startsWith("/") ?
                message.equals(THIS_CMD) : currentCommandName.equals(THIS_CMD);
    }


    @Override
    public boolean cleanAllData() {
        cardDraftRepository.deleteAll();
        return cardDraftRepository.getFirstDraft() == null;
    }
}