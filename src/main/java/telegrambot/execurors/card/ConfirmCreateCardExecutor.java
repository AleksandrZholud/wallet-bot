package telegrambot.execurors.card;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;
import telegrambot.model.drafts.CardDraft;
import telegrambot.model.entity.Card;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;
import telegrambot.service.card.CardService;
import telegrambot.service.card_draft.CardDraftService;
import telegrambot.service.command.CommandService;
import telegrambot.service.current_condition.CurrentConditionService;
import telegrambot.service.state.StateService;
import telegrambot.service.state_history.MsgFromStateHistoryService;

import java.util.Optional;

import static telegrambot.model.enums.CommandEnum.*;
import static telegrambot.model.enums.StateEnum.NO_STATE;

@AllArgsConstructor
@Component
public class ConfirmCreateCardExecutor extends AbstractCommandExecutor {
    private final CardDraftService cardDraftService;
    private final CardService cardService;
    private final CurrentConditionService currentConditionService;
    private final CommandService commandService;
    private final StateService stateService;
    private final MsgFromStateHistoryService msgFromStateHistoryService;

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
        CurrentCondition currentCondition = currentConditionService.getCurrentCondition();
        String currentConditionName = currentCondition.getCommand().getName();

        if (currentConditionName.equals(CREATE_CARD_COMMAND.getCommand())) {
            confirmCard();
        } else {
            UserDataContextHolder.getFacade()
                    .setText("You can not confirm something from command '" + currentConditionName + "'");
        }
    }

    private void confirmCard() {
        Command baseCommand = commandService.getByName(START_COMMAND.getCommand());
        State baseState = stateService.findByName(NO_STATE.getState());
        Optional<CardDraft> draft = Optional.ofNullable(cardDraftService.getFirstDraft());

        Card cardToSave = null;

        if (draft.isEmpty()) {
            processStartCreateCard(baseCommand, baseState);
            return;
        }

        DraftStatus draftStatus = draft.get().getStatus();
        if (draftStatus.equals(DraftStatus.BUILT) || draftStatus.equals(DraftStatus.SAVING)) {
            cardDraftService.updateStatus(DraftStatus.SAVING);
            cardToSave = cardService.createCard(Card.builder()
                    .name(draft.get().getName())
                    .balance(draft.get().getBalance())
                    .build());
        }

        if (cardToSave == null) {
            processErrorCreation();
            return;
        }

        cleanAllData();
        currentConditionService.updateCommandAndState(baseCommand, baseState);

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
        currentConditionService.updateCommandAndState(command, state);
        UserDataContextHolder.getFacade()
                .setText("Seems you have not started creating card.")
                .addButtons(getGlobalCommands());
    }

    @Transactional
    @Override
    public boolean cleanAllData() {
        cardDraftService.deleteAll();
        msgFromStateHistoryService.deleteAll();

        return msgFromStateHistoryService.isEmpty() && cardDraftService.isEmpty();
    }
}