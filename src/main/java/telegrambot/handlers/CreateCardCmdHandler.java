package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.repository.util.CardDraftRepository;
import telegrambot.repository.util.CommandRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.StateRepository;

import java.math.BigDecimal;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CreateCardCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = "/createCard";
    private final CardDraftRepository cardDraftRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;

    @Override
    public SendMessage processMessage() {
        var draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());
        SendMessage sendMessage;

        if (draft.isEmpty()) {
            sendMessage = doCreateCard();
        } else {
            if (Strings.isBlank(draft.get().getName())) {
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
        var cc = currentConditionRepository.getFirst();
        cardDraftRepository.createFirstDraft();
        var cd = cardDraftRepository.getFirstDraft();

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Current Condition: " + cc.getId() + "*" + cc.getCommand().getName() + "*" + cc.getState().getName()
                        + "\nCurrent Draft: " + cd.getId() + "*" + cd.getName() + "*" + cd.getBalance() + "*" + cd.getStatus().name()
                        + "\nEnter Card name:")
                .build();
    }

    private SendMessage doSetName() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName("setBalance");
        var draftName = update.getMessage().getText();

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        var cc = currentConditionRepository.getFirst();
        cardDraftRepository.updateName(draftName);
        var cd = cardDraftRepository.getFirstDraft();

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Current Condition: " + cc.getId() + "*" + cc.getCommand().getName() + "*" + cc.getState().getName()
                        + "\nCurrent Draft: " + cd.getId() + "*" + cd.getName() + "*" + cd.getBalance() + "*" + cd.getStatus().name()
                        + "\nEnter start balance for Card "
                        + draftName + ":")
                .build();
    }

    private SendMessage doSetBalance() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var command = commandRepository.findByName(THIS_CMD);
        var state = stateRepository.findByName("confirmation");
        var draftBalance = BigDecimal.valueOf(Long.parseLong(update.getMessage().getText()));

        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        var cc = currentConditionRepository.getFirst();
        cardDraftRepository.updateBalance(draftBalance);
        cardDraftRepository.updateStatus(DRAFT_STATUS.BUILT.name());
        var cd = cardDraftRepository.getFirstDraft();

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Current Condition: " + cc.getId() + "*" + cc.getCommand().getName() + "*" + cc.getState().getName()
                        + "\nCurrent Draft: " + cd.getId() + "*" + cd.getName() + "*" + cd.getBalance() + "*" + cd.getStatus().name()
                        + "\nConfirm your Card:"
                        + "\nName: " + cd.getName()
                        + "\nBalance: " + cd.getBalance()
                        + "\n/back"
                        + "\n/confirm")
                .build();
    }

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }
}