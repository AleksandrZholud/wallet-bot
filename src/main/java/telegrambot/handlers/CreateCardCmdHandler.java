package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.WalletBot;
import telegrambot.model.util.CardDraft;
import telegrambot.model.util.Command;
import telegrambot.model.util.DRAFT_STATUS;
import telegrambot.model.util.State;
import telegrambot.repository.util.CardDraftRepository;
import telegrambot.repository.util.CommandRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.StateRepository;

import java.math.BigDecimal;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CreateCardCmdHandler extends AbstractCmdHandler {
    @Autowired
    private final WalletBot walletBot;

    private static final String THIS_CMD = "/createCard";
    private final CardDraftRepository cardDraftRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;

    @Override
    public SendMessage processMessage(Update update) throws IllegalAccessException {
        var draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());
        var sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Common sendMessage from CreateCardCmdHandler")
                .build();

        if (draft.isEmpty()) {
            sendMessage = doCreateCard(update);
        } else {
            if (Strings.isBlank(draft.get().getName())) {
                sendMessage = doSetName(update);
            } else sendMessage = doSetBalance(update);
        }

        return sendMessage;
    }

    private SendMessage doCreateCard(Update update) {
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

    private SendMessage doSetName(Update update) {
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

    private SendMessage doSetBalance(Update update) {
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
    public boolean canProcessMessage(Update update) {
        return update.getMessage().getText().equals(THIS_CMD);
    }

}
