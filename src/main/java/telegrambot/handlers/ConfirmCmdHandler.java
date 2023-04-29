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
import telegrambot.repository.util.CardDraftRepository;
import telegrambot.repository.util.CommandRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.StateRepository;
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

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }

    @Transactional
    @Override
    public SendMessage processMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var cc = currentConditionRepository.getFirst();
        SendMessage sendMessage;

        if (cc.getCommand().getName().equals(CREATE_CARD_COMMAND.getCommand())) {
            sendMessage = confirmCard(update);
        } else {
            sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(update,
                    "You can not confirm something from command '" + cc.getCommand().getName() + "'");
        }

        return sendMessage;
    }

    private SendMessage confirmCard(Update update) {
        Command command = commandRepository.findByName(START_COMMAND.getCommand());
        State state = stateRepository.findByName("noState");
        Optional<CardDraft> draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());
        Card cardRes = null;

        if (draft.isEmpty()) {
            return processStartCreateCard(update, command, state);
        }

        if (draft.get().getStatus().equals(DRAFT_STATUS.BUILT)) {
            cardDraftRepository.updateStatus(DRAFT_STATUS.SAVING.name());
            cardRes = cardRepository.save(Card.builder()
                    .name(draft.get().getName())
                    .balance(draft.get().getBalance())
                    .build());
        }

        if (cardRes == null) {
            return processErrorCreation(update);
        }

        cardDraftRepository.deleteAll();
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        return processFinish(update, cardRes);
    }

    private SendMessage processFinish(Update update, Card cardRes) {
        var sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(update,
                "Card " + cardRes.getName() + "successfully saved.\n Good luck!");
        SendMessageUtils.addButtons(sendMessage);
        return sendMessage;
    }

    private SendMessage processErrorCreation(Update update) {
        var sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(update,
                "Something got wrong....\nTap 'Confirm' and try to save again.");
        SendMessageUtils.addButtons(sendMessage, CREATE_CARD_CONFIRM_COMMAND);
        return sendMessage;
    }

    private SendMessage processStartCreateCard(Update update, Command command, State state) {
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        var sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(update,
                "Seems you have not started creating card.\nCreate your Card tapping 'Create card'");
        SendMessageUtils.addButtons(sendMessage, CREATE_CARD_COMMAND);
        return sendMessage;
    }
}