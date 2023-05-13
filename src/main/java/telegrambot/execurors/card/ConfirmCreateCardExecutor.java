package telegrambot.execurors.card;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;
import telegrambot.model.Card;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;
import telegrambot.model.util.drafts.CardDraft;
import telegrambot.repository.CardRepository;
import telegrambot.repository.util.*;

import java.util.Optional;

import static telegrambot.model.enums.CommandEnum.*;
import static telegrambot.model.enums.StateEnum.NO_STATE;

@AllArgsConstructor
@Component
public class ConfirmCreateCardExecutor extends AbstractCommandExecutor {
    private final CardDraftRepository cardDraftRepository;
    private final CardRepository cardRepository;
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    private static final String THIS_CMD = CREATE_CARD_CONFIRM_COMMAND.getCommand();

    @Override
    public boolean isSystemExecutor() {
        return false;
    }

    @Override
    public boolean canExec() {
        return UserDataContextHolder.getInputtedTextCommand().equals(THIS_CMD);
    }

    @Transactional
    @Override
    public void exec() {
        CurrentCondition currentCondition = currentConditionRepository.getCurrentCondition();
        String currentConditionName = currentCondition.getCommand().getName();

        if (currentConditionName.equals(CREATE_CARD_COMMAND.getCommand())) {
            confirmCard();
        } else {
            UserDataContextHolder.getFacade()
                    .setText("You can not confirm something from command '" + currentConditionName + "'");
        }
    }

    private void confirmCard() {
        Command baseCommand = commandRepository.findByName(START_COMMAND.getCommand());
        State baseState = stateRepository.findByName(NO_STATE.getState());
        Optional<CardDraft> draft = Optional.ofNullable(cardDraftRepository.getFirstDraft());

        Card cardToSave = null;

        if (draft.isEmpty()) {
            processStartCreateCard(baseCommand, baseState);
            return;
        }

        DraftStatus draftStatus = draft.get().getStatus();
        if (draftStatus.equals(DraftStatus.BUILT) || draftStatus.equals(DraftStatus.SAVING)) {
            cardDraftRepository.updateStatus(DraftStatus.SAVING.name());
            cardToSave = cardRepository.save(Card.builder()
                    .name(draft.get().getName())
                    .balance(draft.get().getBalance())
                    .build());
        }

        if (cardToSave == null) {
            processErrorCreation();
            return;
        }

        cleanAllData();
        currentConditionRepository.updateCommandAndState(baseCommand.getId(), baseState.getId());

        processFinish(cardToSave);
    }

    private void processFinish(Card cardRes) {
        UserDataContextHolder.getFacade()
                .setText("Card '" + cardRes.getName() + "' successfully saved.\nGood luck!")
                .addButtons(getGlobalCommands());
    }

    private void processErrorCreation() {
        UserDataContextHolder.getFacade()
                .setText("Something got wrong....")
                .addButtons(CREATE_CARD_CONFIRM_COMMAND)
                .addStartButton();
    }

    private void processStartCreateCard(Command command, State state) {
        currentConditionRepository.updateCommandAndState(command.getId(), state.getId());
        UserDataContextHolder.getFacade()
                .setText("Seems you have not started creating card.")
                .addButtons(getGlobalCommands());
    }

    @Transactional
    @Override
    public boolean cleanAllData() {
        cardDraftRepository.deleteAll();
        msgFromStateHistoryRepository.deleteAll();

        return msgFromStateHistoryRepository.findLast() == null && cardDraftRepository.getFirstDraft() == null;
    }
}