package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.Card;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.repository.CardRepository;
import telegrambot.repository.util.CardDraftRepository;
import telegrambot.repository.util.CommandRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.StateRepository;

import java.util.Optional;

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
    public SendMessage processMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var cc = currentConditionRepository.getFirst();
        SendMessage sendMessage;

        if (cc.getCommand().getName().equals("/createCard")) {
            sendMessage = confirmCard(update);
        } else {
            sendMessage = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("You can not confirm something from command '" + cc.getCommand().getName() + "'")
                    .build();
        }

        return sendMessage;
    }

    private SendMessage confirmCard(Update update) {
        var command = commandRepository.findByName("/start");
        var state = stateRepository.findByName("noState");
        var draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());
        Card cardRes = null;

        if (draft.isEmpty()) {
            currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Seems you have not started creating card."
                            + "\nCreate your Card tapping /createCard")
                    .build();
        }

        if (draft.get().getStatus().equals(DRAFT_STATUS.BUILT)) {
            cardDraftRepository.updateStatus(DRAFT_STATUS.SAVING.name());
            cardRes = cardRepository.save(Card.builder()
                    .name(draft.get().getName())
                    .balance(draft.get().getBalance())
                    .isGroupBalance(true)
                    .visibility(true)
                    .build());
        }

        if (cardRes == null) {
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Something got wrong...."
                            + "\nTap /confirm and try to save again.")
                    .build();
        }

        cardDraftRepository.deleteAll();
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Card " + cardRes.getName()
                        + "successfully saved."
                        + "\n Good luck!")
                .build();
    }

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }
}
