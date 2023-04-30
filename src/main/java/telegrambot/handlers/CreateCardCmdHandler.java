package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.*;
import telegrambot.util.SendMessageUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static telegrambot.model.enums.CommandEnum.CREATE_CARD_CONFIRM_COMMAND;

@AllArgsConstructor
@Component
public class CreateCardCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = "/createCard";
    private final CardDraftRepository cardDraftRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public SendMessage processMessage() {
        SendMessage sendMessage;
        var update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();

        if (update.getMessage().getText().equals("/createCard")) {
            cardDraftRepository.deleteAll();
            currentConditionRepository.updateCommandAndState(3L,1L);// TODO: 30.04.2023 1l1l
            msgFromStateHistoryRepository.deleteAll();
        }

        var currentCondition = currentConditionRepository.getFirst();
//        var draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());

        if (currentCondition.getState().getName().equals("noState")) {
            sendMessage = doCreateCard();
        } else {
            if (currentCondition.getState().getName().equals("setName")) {
                sendMessage = doSetName();
            } else {
                sendMessage = doSetBalance();
            }
        }

        return sendMessage;
    }

    private SendMessage doCreateCard() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName("setName");

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.deleteAll();
        cardDraftRepository.createFirstDraft();

        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message("Enter Card name:")
                .build());

        return SendMessageUtils.getSendMessageWithChatIdAndText(update,
                "Enter Card name:");
    }

    private SendMessage doSetName() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName("setBalance");
        var draftName = update.getMessage().getText();

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        cardDraftRepository.updateName(draftName);

        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message("Enter start balance for Card " + draftName + ":")
                .build());

        return SendMessageUtils.getSendMessageWithChatIdAndText(update,
                "Enter start balance for Card " + draftName + ":");
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

        SendMessage sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(update,
                "Confirm your Card:"
                        + "\nName: " + cd.getName()
                        + "\nBalance: " + cd.getBalance());
        SendMessageUtils.addButtons(sendMessage, CREATE_CARD_CONFIRM_COMMAND);

        msgFromStateHistoryRepository.save(MsgFromStateHistory.builder()
                .message(sendMessage.getText())
                .build());

        return sendMessage;
    }

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText().equals(THIS_CMD);
        }
        return false;
    }
}