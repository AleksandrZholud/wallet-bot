package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.Card;
import telegrambot.model.util.CardDraft;
import telegrambot.model.util.Command;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.model.util.State;
import telegrambot.repository.CardRepository;
import telegrambot.repository.util.*;
import telegrambot.util.SendMessageUtils;

import java.util.Optional;

import static telegrambot.model.enums.CommandEnum.*;

@AllArgsConstructor
@Component
public class ConfirmCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = "/confirm";
    private final CardDraftRepository cardDraftRepository;
    private final CardRepository cardRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }

    @Transactional
    @Override
    public SendMessage processMessage() {
        var currentCondition = currentConditionRepository.getFirst();
        SendMessage sendMessage;

        if (currentCondition.getCommand().getName().equals(CREATE_CARD_COMMAND.getCommand())) {
            sendMessage = confirmCard();
        } else {
            sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(
                    "You can not confirm something from command '" + currentCondition.getCommand().getName() + "'");
        }

        return sendMessage;
    }

    private SendMessage confirmCard() {
        var baseCommand = commandRepository.findByName(START_COMMAND.getCommand());
        var baseState = stateRepository.findByName("noState");
        Optional<CardDraft> draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());
        Card cardToSave = null;

        // TODO: 30.04.2023 Check all draft fields after isEmpty
        if (draft.isEmpty()) {
            return processStartCreateCard(baseCommand, baseState);
        }

        if (draft.get().getStatus().equals(DRAFT_STATUS.BUILT) ||
                draft.get().getStatus().equals(DRAFT_STATUS.SAVING)) {
            cardDraftRepository.updateStatus(DRAFT_STATUS.SAVING.name());
            cardToSave = cardRepository.save(Card.builder()
                    .name(draft.get().getName())
                    .balance(draft.get().getBalance())
                    .build());
        }

        if (cardToSave == null) {
            return processErrorCreation();
        }

//        cleanAllData();
        cardDraftRepository.deleteAll();
        msgFromStateHistoryRepository.deleteAll();
        currentConditionRepository.updateCommandAndState(baseCommand.getId(), baseState.getId());

        return processFinish(cardToSave);
    }

    private SendMessage processFinish(Card cardRes) {
        var sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(
                "Card " + cardRes.getName() + " successfully saved.\nGood luck!");
        SendMessageUtils.addButtons(sendMessage, false);
        return sendMessage;
    }

    private SendMessage processErrorCreation() {
        var sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(
                "Something got wrong....\nTap 'Confirm' and try to save again.");
        SendMessageUtils.addButtons(sendMessage, false, CREATE_CARD_CONFIRM_COMMAND);
        return sendMessage;
    }

    private SendMessage processStartCreateCard(Command command, State state) {
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        SendMessage sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(
                "Seems you have not started creating card.\nCreate your Card tapping 'Create card'");
        SendMessageUtils.addButtons(sendMessage, false, CREATE_CARD_COMMAND);
        return sendMessage;
    }

    @Transactional
    @Override
    public boolean cleanAllData() {
        var handlers = AbstractCmdHandler.getAllChildEntities();
        handlers.remove(this);

        for (AbstractCmdHandler handler : handlers) {
            if (!handler.cleanAllData()) {
                return false;
            }
        }

        msgFromStateHistoryRepository.deleteAll();

        return msgFromStateHistoryRepository.findLast() == null && cardDraftRepository.getFirstDraft()==null;
    }
}